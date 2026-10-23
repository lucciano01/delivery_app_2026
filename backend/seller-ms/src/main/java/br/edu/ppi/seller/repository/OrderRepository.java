package br.edu.ppi.seller.repository;

import br.edu.ppi.seller.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    boolean existsByOrderCode(String orderCode);

    Optional<Order> findByOrderCode(String orderCode);
}
