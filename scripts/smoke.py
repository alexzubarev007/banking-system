#!/usr/bin/env python3
"""Run both built jars with a fresh, isolated Compose project and exercise HTTP + RabbitMQ.
Requires Python 3, JDK 25, Docker Compose, and ./gradlew build.
Only resources created under the random project name are removed on exit.
"""
from pathlib import Path
import subprocess, os, socket, time, json, uuid, urllib.request, urllib.parse, urllib.error, http.cookiejar
root = Path(__file__).resolve().parents[1]
project = 'bank-smoke-' + uuid.uuid4().hex[:10]
logs = root / 'build' / project
logs.mkdir(parents=True)
def port():
    with socket.socket() as s:
        s.bind(('127.0.0.1', 0))
        return s.getsockname()[1]
env = os.environ.copy()
env.update(BANK_DB_PORT=str(port()), BANK_RABBIT_PORT=str(port()), BANK_RABBIT_MANAGEMENT_PORT=str(port()), BANK_PORT=str(port()))
env.update(BANK_DB_URL=f"jdbc:postgresql://localhost:{env['BANK_DB_PORT']}/banks_lab", BANK_DB_USER='banks_user', BANK_DB_PASSWORD='banks_password', BANK_RABBIT_HOST='localhost', BANK_RABBIT_USER='guest', BANK_RABBIT_PASSWORD='guest')
env.update(BANK_ADMIN_LOGIN='smoke-admin', BANK_ADMIN_PASSWORD=uuid.uuid4().hex[:24], RATES_CACHE_TTL='2s', RATES_REPLY_TIMEOUT='1s', RATES_PUBLISH_INTERVAL='500ms')
base = 'http://127.0.0.1:' + env['BANK_PORT']
compose = ['docker', 'compose', '-p', project, '-f', str(root/'compose.yaml')]
processes = []
files = []
class NoRedirect(urllib.request.HTTPRedirectHandler):
    def redirect_request(self, *args, **kwargs): return None
def client(): return urllib.request.build_opener(urllib.request.HTTPCookieProcessor(http.cookiejar.CookieJar()), NoRedirect())
def call(opener, method, path, data=None, expected=200, form=False):
    headers = {}
    if data is not None:
        raw = urllib.parse.urlencode(data) if form else json.dumps(data)
        headers['Content-Type'] = 'application/x-www-form-urlencoded' if form else 'application/json'
        data = raw.encode()
    req = urllib.request.Request(base+path, data=data, headers=headers, method=method)
    try:
        with opener.open(req, timeout=10) as response: status, body = response.status, response.read()
    except urllib.error.HTTPError as error: status, body = error.code, error.read()
    assert status == expected, f'{method} {path}: expected {expected}, got {status}: {body[:200]!r}'
    if not body: return None
    try: return json.loads(body)
    except ValueError: return body.decode(errors='replace')
try:
    subprocess.run(compose+['up','-d','--wait'], cwd=root, env=env, check=True, timeout=120)
    for service in ['rates-service','bank-service']:
        jars = [p for p in (root/service/'presentation/build/libs').glob('*.jar') if not p.name.endswith('-plain.jar')]
        assert len(jars)==1, f'Build {service} first'
        log=(logs/(service+'.log')).open('w'); files.append(log)
        processes.append(subprocess.Popen(['java','-jar',str(jars[0])],cwd=root,env=env,stdout=log,stderr=subprocess.STDOUT))
    admin=client()
    deadline=time.monotonic()+60
    while True:
        assert all(p.poll() is None for p in processes), f'Application exited; see {logs}'
        try:
            call(admin,'GET','/login'); break
        except (OSError,AssertionError):
            if time.monotonic()>deadline: raise
            time.sleep(.5)
    # Readiness of HTTP alone may precede the bootstrap runner by a few milliseconds.
    for attempt in range(20):
        call(admin,'POST','/login',{'username':env['BANK_ADMIN_LOGIN'],'password':env['BANK_ADMIN_PASSWORD']},expected=302,form=True)
        try: call(admin,'GET','/api/accounts/all');break
        except AssertionError:
            if attempt==19:raise
            time.sleep(.2)
    call(client(),'GET','/api/accounts/all',expected=401)
    call(admin,'GET','/v3/api-docs')
    password=uuid.uuid4().hex[:20]
    user=call(admin,'POST','/api/registration/client',{'login':'alice','password':password,'name':'Alice','age':25,'gender':'FEMALE','hairColor':'black'})
    alice=client();call(alice,'POST','/login',{'username':'alice','password':password},expected=302,form=True)
    first=call(alice,'POST','/api/accounts',expected=201)['id']
    second=call(alice,'POST','/api/accounts',expected=201)['id']
    call(alice,'PATCH','/api/accounts/put',{'accountId':first,'money':100})
    call(alice,'PATCH','/api/accounts/transfer',{'senderId':first,'recipientId':second,'money':10})
    assert call(alice,'GET',f'/api/accounts/{first}/balance')==90
    assert call(alice,'GET',f'/api/accounts/{second}/balance')==10
    assert len(call(alice,'GET',f'/api/operation/{first}'))==2
    call(alice,'PATCH','/api/accounts/transfer',{'senderId':first,'recipientId':second,'money':-10},expected=400)
    call(alice,'GET',f'/api/users/friends/{uuid.uuid4()}',expected=403)
    converted=call(alice,'GET',f'/api/accounts/{first}/balance?currencyCode=USD')
    assert converted>0
    call(alice,'GET',f'/api/accounts/{first}/balance?currencyCode=ZZZ',expected=503)
    # Stop only the supplier process started by this script, let the quote expire.
    processes[0].terminate();processes[0].wait(timeout=15);time.sleep(2.2)
    call(alice,'GET',f'/api/accounts/{first}/balance?currencyCode=USD',expected=503)
    call(alice,'PATCH','/api/accounts/put',{'accountId':first,'money':1})
    assert call(alice,'GET',f'/api/accounts/{first}/balance')==91
    print(json.dumps({'result':'passed','checks':['schema validation','bootstrap and login','OpenAPI','registration','accounts','transfer','history','validation','ownership','RabbitMQ conversion','supplier outage and ruble operations'],'logs':str(logs)},ensure_ascii=False))
finally:
    for process in processes:
        if process.poll() is None:
            process.terminate()
            try: process.wait(timeout=15)
            except subprocess.TimeoutExpired: process.kill();process.wait()
    for file in files: file.close()
    subprocess.run(compose+['down','--volumes'],cwd=root,env=env,check=False,timeout=60)
