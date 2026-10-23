package br.edu.catolica.delivery.app.email.service;

import br.edu.catolica.delivery.app.email.constants.EmailConstants;
import br.edu.catolica.delivery.app.email.domain.Email;
import br.edu.catolica.delivery.app.email.dto.OrderItemNotificationDTO;
import br.edu.catolica.delivery.app.email.dto.OrderNotificationEvent;
import br.edu.catolica.delivery.app.email.exception.EmailFileTemplateException;
import br.edu.catolica.delivery.app.email.repository.EmailRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Locale;

import static br.edu.catolica.delivery.app.email.constants.EmailConstants.EMAIL_FROM;
import static br.edu.catolica.delivery.app.email.constants.EmailConstants.EMAIL_SUBJECT;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final EmailRepository emailRepository;
    // não pode ser final: o @RequiredArgsConstructor do Lombok não copia o @Value
    // para o parâmetro do construtor, então o Spring não conseguiria injetar o Resource
    @Value("classpath:templates/order-email.txt")
    private Resource resource;
    private NumberFormat numberFormat;
    private String emailTemplate;

    @PostConstruct
    public void init() throws IOException {
        this.emailTemplate = resource.getContentAsString(StandardCharsets.UTF_8);
        this.numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    }

    public void sendOrderReceivedEmail(OrderNotificationEvent event) {
        String subject = EMAIL_SUBJECT.concat(event.orderCode());
        String body = buildBody(event);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(EMAIL_FROM);
        message.setTo(event.customerEmail());
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);

        emailRepository.save(Email.builder()
                .to(event.customerEmail())
                .subject(subject)
                .orderCode(event.orderCode())
                .body(body)
                .build());

        log.info("Email de pedido recebido enviado para {} referente ao pedido {}",
                event.customerEmail(), event.orderCode());
    }

    private String buildBody(OrderNotificationEvent event) {
      try {
          String formattedItems  = String.join("\n",
                  event.items().stream()
                          .map(item -> "- %s (x%d) - %s".formatted(
                                  item.productName(),
                                  item.quantity(),
                                  numberFormat.format(item.subAmount())
                          )).toList());

          String formattedTotalAmount = numberFormat.format(event.totalAmount());

          return emailTemplate.formatted(
                  event.customerName(),
                  event.sellerName(),
                  formattedItems,
                  formattedTotalAmount
          );
      } catch (Exception e) {
          log.error("m=buildBody, failed to try generate email to order code = {}", event.orderCode());
          throw new EmailFileTemplateException(e.getMessage());
      }


//        StringBuilder body = new StringBuilder();
//        body.append("Olá, ").append(event.customerName()).append("!\n\n");
//        body.append("Seu pedido foi recebido pelo estabelecimento ").append(event.sellerName()).append(".\n\n");
//        body.append("Itens do pedido:\n");
//
//        for (OrderItemNotificationDTO item : event.items()) {
//            body.append("- ").append(item.productName())
//                    .append(" (x").append(item.quantity()).append(")")
//                    .append(" - R$ ").append(item.subAmount())
//                    .append("\n");
//        }
//
//        body.append("\nTotal do pedido: R$ ").append(event.totalAmount());
//        return body.toString();
    }
}
