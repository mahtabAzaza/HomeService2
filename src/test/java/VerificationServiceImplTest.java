
import ir.HomeServiceApplication.entity.Specialist;
import ir.HomeServiceApplication.entity.SpecialistStatus;
import ir.HomeServiceApplication.entity.VerificationToken;
import ir.HomeServiceApplication.repository.VerificationTokenRepository;
import ir.HomeServiceApplication.service.serviceImpl.EmailService;
import ir.HomeServiceApplication.service.serviceImpl.VerificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationServiceImplTest {

    @Mock private VerificationTokenRepository verificationTokenRepository;
    @Mock private EmailService emailService;

    @InjectMocks
    private VerificationServiceImpl verificationService;

    // =====================================================
    // REFRESH APPROVAL ELIGIBILITY
    // =====================================================

    // Promotes to WAITING_FOR_APPROVAL once both the email is verified and a photo is present
    @Test
    void refreshApprovalEligibility_shouldPromote_whenEmailVerifiedAndPhotoPresent() {
        Specialist specialist = new Specialist();
        specialist.setEmailVerified(true);
        specialist.setProfileImage(new byte[]{1, 2, 3});
        specialist.setStatus(SpecialistStatus.NEW);

        verificationService.refreshApprovalEligibility(specialist);

        assertEquals(SpecialistStatus.WAITING_FOR_APPROVAL, specialist.getStatus());
    }

    // Leaves status unchanged when the email is not verified yet, even with a photo present
    @Test
    void refreshApprovalEligibility_shouldNotPromote_whenEmailNotVerified() {
        Specialist specialist = new Specialist();
        specialist.setEmailVerified(false);
        specialist.setProfileImage(new byte[]{1, 2, 3});
        specialist.setStatus(SpecialistStatus.NEW);

        verificationService.refreshApprovalEligibility(specialist);

        assertEquals(SpecialistStatus.NEW, specialist.getStatus());
    }

    // Leaves status unchanged when there is no profile photo yet, even with a verified email
    @Test
    void refreshApprovalEligibility_shouldNotPromote_whenNoPhoto() {
        Specialist specialist = new Specialist();
        specialist.setEmailVerified(true);
        specialist.setProfileImage(null);
        specialist.setStatus(SpecialistStatus.NEW);

        verificationService.refreshApprovalEligibility(specialist);

        assertEquals(SpecialistStatus.NEW, specialist.getStatus());
    }

    // =====================================================
    // ISSUE VERIFICATION TOKEN
    // =====================================================

    // Saves a fresh, unused token tied to the user and emails the verification link to their address
    @Test
    void issueVerificationToken_shouldSaveTokenAndSendEmail() {
        Specialist specialist = new Specialist();
        specialist.setEmail("spec@mail.com");

        when(verificationTokenRepository.save(any(VerificationToken.class)))
                .thenAnswer(i -> i.getArgument(0));

        verificationService.issueVerificationToken(specialist);

        ArgumentCaptor<VerificationToken> tokenCaptor = ArgumentCaptor.forClass(VerificationToken.class);
        verify(verificationTokenRepository).save(tokenCaptor.capture());

        VerificationToken savedToken = tokenCaptor.getValue();
        assertNotNull(savedToken.getToken());
        assertFalse(savedToken.isUsed());
        assertNotNull(savedToken.getExpiryDate());
        assertEquals(specialist, savedToken.getUser());

        verify(emailService).sendVerificationEmail("spec@mail.com", savedToken.getToken());
    }
}
