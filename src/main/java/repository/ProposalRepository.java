package repository;

import entity.Order;
import entity.Proposal;
import entity.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

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
