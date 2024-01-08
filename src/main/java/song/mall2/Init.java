package song.mall2;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import song.mall2.domain.cart.entity.Cart;
import song.mall2.domain.cart.repository.CartJpaRepository;
import song.mall2.domain.order.entity.OrderProduct;
import song.mall2.domain.order.entity.Orders;
import song.mall2.domain.order.repository.OrderProductJpaRepository;
import song.mall2.domain.order.repository.OrdersJpaRepository;
import song.mall2.domain.payment.entity.Payment;
import song.mall2.domain.payment.repository.PaymentJpaRepository;
import song.mall2.domain.product.entity.Product;
import song.mall2.domain.product.repository.ProductJpaRepository;
import song.mall2.domain.user.entity.User;
import song.mall2.domain.user.entity.UserRole;
import song.mall2.domain.user.repository.UserJpaRepository;
import song.mall2.domain.user.repository.UserRoleJpaRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static song.mall2.domain.user.entity.UserRole.Role.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class Init {
    private final InitService initService;

    @PostConstruct
    public void setInit() {
        initService.setData();
    }

    @Component
    @RequiredArgsConstructor
    private static class InitService {
        private final PasswordEncoder passwordEncoder;
        private final UserJpaRepository userRepository;
        private final UserRoleJpaRepository userRoleRepository;
        private final ProductJpaRepository productRepository;
        private final CartJpaRepository cartRepository;
        private final OrdersJpaRepository ordersRepository;
        private final OrderProductJpaRepository orderProductRepository;
        private final PaymentJpaRepository paymentRepository;

        @Transactional
        public void setData() {
            User userA = saveUser("a", "a", "nameA", "test1@email.com");
            User userB = saveUser("b", "b", "nameB", "test1@email.com");

            Product productA = saveProduct(userA, "productA", 10, "This is productA", "/file/downloadFile/spring.png", 1000);
            Product productB = saveProduct(userA, "productB", 5, "This is productB", "/file/downloadFile/security.png", 300);

            addCart(userA, productA, 10);
            addCart(userA, productB, 10);

            saveOrder(userA, productA, 10);
        }

        private User saveUser(String username, String password, String name, String email) {
            User user = User.create(username, passwordEncoder.encode(password), name, email);

            User saveUser = userRepository.save(user);

            grantRole(user);

            return saveUser;
        }

        private void grantRole(User user) {
            UserRole userRole = UserRole.create(user, ROLE_USER.name());
            userRoleRepository.save(userRole);
        }

        private Product saveProduct(User userId, String name, Integer price, String description, String thumbnailUrl, Integer stockQuantity) {
            Product product = Product.create(userId, name, price, description, thumbnailUrl, null, stockQuantity, Product.Category.A.name());

            return productRepository.save(product);
        }

        private Cart addCart(User user, Product product, Integer quantity) {
            Cart cart = Cart.create(user, product, quantity);

            return cartRepository.save(cart);
        }

        private Orders saveOrder(User user, Product product, Integer quantity) {
            Orders orders = Orders.create(user);

            List<OrderProduct> orderProductList = new ArrayList<>();
            OrderProduct orderProduct = OrderProduct.create(orders, product, user, quantity);
            orderProductList.add(orderProduct);

            Orders saveOrders = ordersRepository.save(orders);
            List<OrderProduct> saveOrderProductList = orderProductRepository.saveAll(orderProductList);

            Payment payment = Payment.of(user, orders, "testPayment", "PAID", orderProduct.getAmount(), DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm").format(LocalDateTime.now()), null, null);
            Payment savePayment = paymentRepository.save(payment);

            return saveOrders;
        }
    }
}
