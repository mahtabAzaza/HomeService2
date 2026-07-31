package ir.HomeServiceApplication.service.serviceImpl;

import ir.HomeServiceApplication.entity.Specialist;
import ir.HomeServiceApplication.entity.SpecialistStatus;
import ir.HomeServiceApplication.service.VerificationService;
import org.springframework.stereotype.Service;

@Service
public class VerificationServiceImpl implements VerificationService {

    @Override
    public void refreshApprovalEligibility(Specialist specialist) {
        if (specialist.isEmailVerified() && specialist.getProfileImage() != null) {
            specialist.setStatus(SpecialistStatus.WAITING_FOR_APPROVAL);
        }
    }
}
