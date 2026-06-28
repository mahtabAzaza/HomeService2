package repository;
import entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceRepository extends
        JpaRepository<Service,Long>
//        ,        JpaSpecificationExecutor<Manager>
        {

                Service findByParentService(Service parentService);

}
