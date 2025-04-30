package com.small.ecommerce_chatbot.entity;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 自增主键

    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId; // 订单业务 ID

    @Column(name = "placed_date", nullable = false)
    private LocalDateTime placedDate; // 下单日期

    @Column(nullable = false)
    private double subtotal; // 小计

    @Column(nullable = false)
    private double shipping; // 运费

    @Column(nullable = false)
    private double total; // 总金额

    @Column(name = "billing_address_id", nullable = false)
    private Long billingAddressId; // 账单地址 ID (手动管理)

    @Column(name = "shipping_address_id", nullable = false)
    private Long shippingAddressId; // 收货地址 ID (手动管理)

    @Transient
    private List<OrderItem> items; // 临时存储订单项

    @Transient
    private Address billingAddress; // 临时存储账单地址

    @Transient
    private Address shippingAddress; // 临时存储收货地址

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getPlacedDate() {
        return placedDate;
    }

    public void setPlacedDate(LocalDateTime placedDate) {
        this.placedDate = placedDate;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getShipping() {
        return shipping;
    }

    public void setShipping(double shipping) {
        this.shipping = shipping;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Long getBillingAddressId() {
        return billingAddressId;
    }

    public void setBillingAddressId(Long billingAddressId) {
        this.billingAddressId = billingAddressId;
    }

    public Long getShippingAddressId() {
        return shippingAddressId;
    }

    public void setShippingAddressId(Long shippingAddressId) {
        this.shippingAddressId = shippingAddressId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}

