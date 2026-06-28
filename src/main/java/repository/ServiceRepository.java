package repository;
import entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ServiceRepository extends
        JpaRepository<Service,Long>
//        ,        JpaSpecificationExecutor<Manager>
        {

                List<Service> findByParentService(Service parentService);
                List<Service> findByParentServiceIsNull();

}
