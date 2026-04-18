package Services;

import Mapping.OperationMapping;
import Operations.Operation;
import Operations.OperationDto;
import Operations.Requests.FindByTypeAndAccountRequest;
import Operations.Requests.GetHistoryRequest;
import Repositories.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationService {
    private final OperationRepository operationRepository;

    public List<OperationDto> getHistory(GetHistoryRequest request) {
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