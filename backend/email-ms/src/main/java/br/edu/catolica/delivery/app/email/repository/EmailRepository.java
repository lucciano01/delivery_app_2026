package br.edu.catolica.delivery.app.email.repository;

import br.edu.catolica.delivery.app.email.domain.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email, Long> {
}
