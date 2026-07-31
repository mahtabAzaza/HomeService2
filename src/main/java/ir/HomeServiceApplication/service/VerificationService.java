package ir.HomeServiceApplication.service;

import ir.HomeServiceApplication.entity.Specialist;
import ir.HomeServiceApplication.entity.User;

public interface VerificationService {

    // Promotes a specialist to WAITING_FOR_APPROVAL once both the email is
    // verified and a profile photo has been uploaded. Used by both signup
    // and updateProfile so the rule stays consistent everywhere.
    void refreshApprovalEligibility(Specialist specialist);

    // Creates a verification token for the user and emails the verification
    // link. Used on signup and whenever a user changes their email address.
    void issueVerificationToken(User user);
}
