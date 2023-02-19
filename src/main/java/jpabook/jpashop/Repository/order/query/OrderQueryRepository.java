package jpabook.jpashop.Repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos(){
        List<OrderQueryDto> result = findOrders();

        result.forEach(o->{
            List<OrderItemQueryDto> orderItems=findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });

        return result;
    }
    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> orders = findOrders();

        List<Long> ids = getOrderIds(orders);
        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMaps(ids);


        orders.forEach(o->o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return orders;



    }
    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery("select new jpabook.jpashop.Repository.order.query.OrderFlatDto" +
                "(o.id,m.name,o.orderDate,o.status,d.address,i.name,oi.orderPrice,oi.count)" +
                " from Order o" +
                " join o.member m" +
                " join o.delivery d" +
                " join o.orderItems oi" +
                " join oi.item i",OrderFlatDto.class).getResultList();

    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery("select new jpabook.jpashop.Repository.order.query.OrderItemQueryDto" +
                "(oi.order.id,i.name,oi.orderPrice,oi.count)" +
                " from OrderItem  oi" +
                " join oi.item i" +
                " where oi.order.id=:orderId")
                .setParameter("orderId",orderId).getResultList();

    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery("select new jpabook.jpashop.Repository.order.query.OrderQueryDto" +
                "(o.id,m.name,o.orderDate,o.status,d.address)" +
                " from Order o" +
                " join o.member m" +
                " join o.delivery d", OrderQueryDto.class).getResultList();
    }



    private Map<Long, List<OrderItemQueryDto>> findOrderItemMaps(List<Long> ids) {
        return em.createQuery("select new jpabook.jpashop.Repository.order.query.OrderItemQueryDto" +
                        "(oi.order.id,i.name,oi.orderPrice,oi.count)" +
                        " from OrderItem  oi" +
                        " join oi.item i" +
                        " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", ids).getResultList().stream().collect(Collectors.groupingBy(oi -> oi.getOrderId()));
    }

    private List<Long> getOrderIds(List<OrderQueryDto> orders) {
        return orders.stream().map(o -> o.getOrderId()).collect(Collectors.toList());
    }


}
