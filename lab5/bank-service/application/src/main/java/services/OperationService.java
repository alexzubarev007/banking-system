package services;

import accounts.Account;
import authentifications.Authentication;
import authentifications.Role;
import mapping.OperationMapping;
import operations.Operation;
import operations.OperationDto;
import operations.requests.FindByTypeAndAccountRequest;
import operations.requests.GetHistoryRequest;
import org.springframework.security.core.userdetails.User;
import repositories.AccountRepository;
import repositories.AuthentificationRepository;
import repositories.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import services.exceptions.NotFoundException;
import services.exceptions.OtherDataException;
import services.exceptions.UnauthorizedException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationService {
    private final OperationRepository operationRepository;
    private final AuthentificationRepository authentificationRepository;
    private final AccountRepository accountRepository;

    public List<OperationDto> getHistory(GetHistoryRequest request,
                                         User userDetails)
            throws UnauthorizedException,
            NotFoundException {
        Authentication authentication = authentificationRepository
                .findByLogin(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Incorrect login"));

        Account account = accountRepository
                .findById(request.accountId())
                .orElseThrow(() -> new NotFoundException("Account not found"));


        if (authentication.userId() != account.getUserId()
                && authentication.role() == Role.CLIENT) {
            throw new OtherDataException("Try to read other data!");
        }

        List<Operation> operations = operationRepository
                .findByAccountId(request.accountId());

        return operations
                .stream()
                .map(OperationMapping::mapToDto)
                .toList();
    }

    public List<OperationDto> findByTypeAndAccount(FindByTypeAndAccountRequest request) {
        return operationRepository
                .findByTypeAndAccountId(request.type(), request.accountId())
                .stream()
                .map(OperationMapping::mapToDto)
                .toList();
    }
}