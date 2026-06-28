package repository;


import entity.Specialist;

public interface SpecialistRepository extends
        BaseRepository<Specialist,Long> {
    Specialist findByEmail(String email);

}
