package ir.HomeServiceApplication.repository;
import ir.HomeServiceApplication.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends
        JpaRepository<Service,Long>
//        ,        JpaSpecificationExecutor<Manager>
        {

                List<Service> findByParentService(Service parentService);
                List<Service> findByParentServiceIsNull();

}
