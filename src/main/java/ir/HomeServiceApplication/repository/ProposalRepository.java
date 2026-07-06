package ir.HomeServiceApplication.repository;

import ir.HomeServiceApplication.entity.Order;
import ir.HomeServiceApplication.entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProposalRepository extends
        JpaRepository<Proposal,Long>
//        , JpaSpecificationExecutor<Specialist>
{
    // to get all proposals for an order (customer sees them, picks one)
    List<Proposal> findByOrder(Order order);
    // to sort proposals by price or to check for duplicate submissions
    List<Proposal> findByOrderOrderByProposalPriceAsc(Order order);
}
