package io.farmx.service;

import io.farmx.dto.FarmDTO;
import io.farmx.model.Farm;
import io.farmx.model.UserEntity;
import io.farmx.repository.FarmRepository;
import io.farmx.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FarmService {

    private final FarmRepository farmRepo;
    private final UserRepository userRepo;

    public FarmService(FarmRepository farmRepo, UserRepository userRepo) {
        this.farmRepo = farmRepo;
        this.userRepo = userRepo;
    }

    public List<FarmDTO> getMyFarms(Principal principal) {
        UserEntity user = userRepo.findByEmail(principal.getName()).orElseThrow();
        return farmRepo.findAllByUser(user).stream().map(this::toDto).collect(Collectors.toList());
    }

    public FarmDTO createFarm(FarmDTO dto, Principal principal) {
        UserEntity user = userRepo.findByEmail(principal.getName()).orElseThrow();
        Farm farm = fromDto(dto);
        farm.setUser(user);
        return toDto(farmRepo.save(farm));
    }

    public void deleteFarm(Long id, Principal principal) {
        UserEntity user = userRepo.findByEmail(principal.getName()).orElseThrow();
        Farm farm = farmRepo.findById(id).orElseThrow();

        if (farm.getUser().getId() != user.getId()) {
            throw new RuntimeException("Unauthorized");
        }

        farmRepo.deleteById(id);
    }

    private FarmDTO toDto(Farm farm) {
        return new FarmDTO(farm.getId(), farm.getName(), farm.getLocation(), farm.getAreaSize());
    }

    private Farm fromDto(FarmDTO dto) {
        Farm farm = new Farm();
        farm.setId(dto.getId());
        farm.setName(dto.getName());
        farm.setLocation(dto.getLocation());
        farm.setAreaSize(dto.getAreaSize());
        return farm;
    }
}
