package Services;

import Mapping.OperationMapping;
import Operations.Operation;
import Operations.OperationDto;
import Operations.Requests.GetHistoryRequest;
import Repositories.OperationRepository;
import lombok.AllArgsConstructor;
import java.util.List;

@AllArgsConstructor
public class OperationService {
    private OperationRepository operationRepository;

    public List<OperationDto> getHistory(GetHistoryRequest request) {
        List<Operation> operations = operationRepository
                .findByAccountId(request.accountId());

        return operations
                .stream()
                .map(OperationMapping::mapToDto)
                .toList();
    }
}