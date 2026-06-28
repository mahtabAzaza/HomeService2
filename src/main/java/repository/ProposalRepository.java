package repository;

import entity.Proposal;
import entity.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProposalRepository extends
        JpaRepository<Proposal,Long>
//        , JpaSpecificationExecutor<Specialist>
{

}
