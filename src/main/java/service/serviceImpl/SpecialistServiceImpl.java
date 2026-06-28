package service.serviceImpl;

import DTO.SpecialistDto;
import DTO.SpecialistSignupDto;
import entity.*;
import repository.OrderRepository;
import repository.ProposalRepository;
import repository.SpecialistRepository;
import repository.WalletRepository;
import service.SpecialistService;

import java.time.LocalDateTime;

public class SpecialistServiceImpl implements SpecialistService {

    private final SpecialistRepository specialistRepository;
    private final ProposalRepository proposalRepository;
    private final OrderRepository orderRepository;
    private final WalletRepository walletRepository;

    public SpecialistServiceImpl(SpecialistRepository specialistRepository,
                                 ProposalRepository proposalRepository,
                                 OrderRepository orderRepository,
                                 WalletRepository walletRepository) {

        this.specialistRepository = specialistRepository;
        this.proposalRepository = proposalRepository;
        this.orderRepository = orderRepository;
        this.walletRepository = walletRepository;
    }

    @Override
    public void signup(SpecialistSignupDto dto) {

        Specialist specialist = SpecialistMapper.toEntity(dto);

        specialist.setRegisterDate(LocalDateTime.now());

        specialist.setStatus(SpecialistStatus.WAITING_FOR_APPROVAL);

        specialistRepository.save(specialist);
    }


    @Override
    public Specialist login(String email, String password) {

        Specialist specialist =
                specialistRepository.findByEmail(email);

        if (specialist == null) {
            throw new RuntimeException("Specialist not found");
        }

        if (!specialist.getPassword().equals(password)) {
            throw new RuntimeException("Wrong password");
        }

        if (specialist.getStatus() != SpecialistStatus.APPROVED) {
            throw new RuntimeException("Account not approved");
        }

        return specialist;
    }


    @Override
    public void updateProfile(SpecialistDto dto) {

        Specialist specialist =
                specialistRepository.findById(dto.getId());

        if (specialist == null) {
            throw new RuntimeException("Specialist not found");
        }

        specialist.setEmail(dto.getEmail());

        specialist.setProfileImage(dto.getProfileImage());

        specialist.setStatus(SpecialistStatus.WAITING_FOR_APPROVAL);

        specialistRepository.update(specialist);

    }


    @Override
    public void submitProposal(Proposal proposal) {

        Order order =
                orderRepository.findById(
                        proposal.getOrder().getOrderId());

        if (order == null) {
            throw new RuntimeException("Order not found");
        }


        proposal.setProposalRegistrationDate(LocalDateTime.now());

        proposalRepository.save(proposal);

    }



    @Override
    public double getWalletBalance(Long specialistId) {

        Wallet wallet =
                walletRepository.findById(specialistId);

        return wallet.getBalance();

    }



    @Override
    public void withdraw(Long specialistId,
                         double amount) {

        Wallet wallet =
                walletRepository.findById(specialistId);


        if (wallet.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }


        wallet.setBalance(
                (long) (wallet.getBalance() - amount)
        );


        walletRepository.update(wallet);

    }


}