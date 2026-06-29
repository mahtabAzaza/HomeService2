package service;

@Service
@RequiredArgsConstructor
@Transactional
public class ServiceService {

    private final ServiceRepository serviceRepository;

    // افزودن سرویس
    public void createService(Service service) {

        if (service == null) {
            throw new InvalidOperationException();
        }

        if (service.getServiceName() == null || service.getServiceName().isBlank()) {
            throw new InvalidOperationException();
        }

        serviceRepository.save(service);
    }

    // ویرایش سرویس
    public void updateService(Service service) {

        Service existing = serviceRepository.findById(service.getServiceId())
                .orElseThrow(ServiceNotFoundException::new);

        existing.setServiceName(service.getServiceName());
        existing.setServiceDescription(service.getServiceDescription());
        existing.setServiceBasePrice(service.getServiceBasePrice());
        existing.setParentService(service.getParentService());
    }

    // حذف سرویس
    public void deleteService(Long id) {

        if (!serviceRepository.existsById(id)) {
            throw new ServiceNotFoundException();
        }

        serviceRepository.deleteById(id);
    }

    // نمایش همه سرویس‌ها
    @Transactional(readOnly = true)
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    // پیدا کردن سرویس با شناسه
    @Transactional(readOnly = true)
    public Service getServiceById(Long id) {

        return serviceRepository.findById(id)
                .orElseThrow(ServiceNotFoundException::new);
    }
}