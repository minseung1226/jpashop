package jpabook.jpashop.service;

import jpabook.jpashop.Repository.OrderRepository;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    OrderService orderService;
    @Autowired
    EntityManager em;

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문(){
        Member member = createMember("회원1");
        em.persist(member);

        Book book = createBook("시골 JPA", 10000, 10);
        em.persist(book);

        int orderCount=2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        Order findOrder = orderRepository.findOne(orderId);

        assertThat(findOrder.getId()).isEqualTo(orderId);
        assertThat(findOrder.getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(findOrder.getTotalPrice()).isEqualTo(10000*orderCount);
        assertThat(book.getStockQuantity()).isEqualTo(10-orderCount);


    }

    @Test
    public void 상품주문_재고수량초과(){
        Member member = createMember("회원1");
        em.persist(member);
        Book book = createBook("시골 JPA", 10000, 10);
        em.persist(book);

        assertThatThrownBy(()->orderService.order(member.getId(), book.getId(),11))
                .isInstanceOf(NotEnoughStockException.class);


    }

    @Test
    public void 주문취소(){
        Member member = createMember("회원1");
        em.persist(member);
        Book book = createBook("상품1", 10000, 10);
        em.persist(book);

        Long orderId = orderService.order(member.getId(), book.getId(), 2);


        orderService.cancelOrder(orderId);

        Order findOrder = orderRepository.findOne(orderId);


        assertThat(findOrder.getStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(book.getStockQuantity()).isEqualTo(10);
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        return book;
    }

    private Member createMember(String name) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(new Address("서울","강가","123-123"));
        return member;
    }



}