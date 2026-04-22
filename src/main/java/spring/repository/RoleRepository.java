package spring.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import spring.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

	Optional<Role> findByName(String name);

	@Override
	Page<Role> findAll(Pageable pageable);
}
