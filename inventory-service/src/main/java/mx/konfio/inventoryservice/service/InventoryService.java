package mx.konfio.inventoryservice.service;

import lombok.RequiredArgsConstructor;
import mx.konfio.inventoryservice.dto.InventoryResponse;
import mx.konfio.inventoryservice.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCode){
        return inventoryRepository.findBySkuCodeIn(skuCode).stream().map(inventory ->
            InventoryResponse.builder().skucode(inventory.getSkuCode())
                    .isInStock(inventory.getQuantity() > 0)
                    .build()
        ).toList();
    }
}
