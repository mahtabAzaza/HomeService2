package ir.HomeServiceApplication.service;

import ir.HomeServiceApplication.entity.Specialist;
import ir.HomeServiceApplication.entity.SpecialistStatus;
import ir.HomeServiceApplication.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ir.HomeServiceApplication.repository.SpecialistRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ApprovalService {

    private final SpecialistRepository specialistRepository;

    public void approveSpecialist(Long specialistId) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new NotFoundException("Specialist not found"));

        if (specialist.getStatus() == SpecialistStatus.APPROVED) {
            return;
        }

        specialist.setStatus(SpecialistStatus.APPROVED);
    }

    public void sendToWaitingApproval(Long specialistId) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new NotFoundException("Specialist not found"));

        if (specialist.getStatus() == SpecialistStatus.WAITING_FOR_APPROVAL) {
            return;
        }

        specialist.setStatus(SpecialistStatus.WAITING_FOR_APPROVAL);
    }
}
