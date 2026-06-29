
package service;
@Service
@RequiredArgsConstructor
@Transactional
public class ApprovalService {

    private final SpecialistRepository specialistRepository;

    /**
     * Approve specialist by manager
     */
    public void approveSpecialist(Long specialistId) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(SpecialistNotFoundException::new);

        if (specialist.getStatus() == SpecialistStatus.APPROVED) {
            return;
        }

        specialist.setStatus(SpecialistStatus.APPROVED);
    }

    /**
     * Put specialist back to waiting for approval
     */
    public void sendToWaitingApproval(Long specialistId) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(SpecialistNotFoundException::new);

        if (specialist.getStatus() == SpecialistStatus.WAITING_FOR_APPROVAL) {
            return;
        }

        specialist.setStatus(SpecialistStatus.WAITING_FOR_APPROVAL);
    }
}