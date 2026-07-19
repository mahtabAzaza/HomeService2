package ir.HomeServiceApplication.repository;

import ir.HomeServiceApplication.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends
        JpaRepository<Service, Long>
//        ,        JpaSpecificationExecutor<Manager>
{

    @EntityGraph(attributePaths = "childServices")
    Page<Service> findByParentServiceIsNull(Pageable pageable);

}
