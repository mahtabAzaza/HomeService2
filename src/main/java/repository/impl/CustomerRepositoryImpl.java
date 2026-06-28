package repository.impl;

import entity.Customer;
import entity.Order;
import org.hibernate.Session;
import org.hibernate.Transaction;
import repository.CustomerRepository;
import util.HibernateUtil;

import java.util.List;

public class CustomerRepositoryImpl  {

//    @Override
//    public void save(Order customer) {
//
//        Session session = HibernateUtil.getSessionFactory().openSession();
//        Transaction transaction = session.beginTransaction();
//
//        session.persist(customer);
//
//        transaction.commit();
//        session.close();
//    }
//
//    @Override
//    public void update(Customer customer) {
//
//        Session session = HibernateUtil.getSessionFactory().openSession();
//        Transaction transaction = session.beginTransaction();
//
//        session.merge(customer);
//
//        transaction.commit();
//        session.close();
//    }
//
//    @Override
//    public void delete(Customer customer) {
//
//        Session session = HibernateUtil.getSessionFactory().openSession();
//        Transaction transaction = session.beginTransaction();
//
//        session.remove(customer);
//
//        transaction.commit();
//        session.close();
//    }
//
//    @Override
//    public Customer findById(Long id) {
//
//        Session session = HibernateUtil.getSessionFactory().openSession();
//
//        Customer customer = session.get(Customer.class, id);
//
//        session.close();
//
//        return customer;
//    }
//
//    @Override
//    public List<Customer> findAll() {
//
//        Session session = HibernateUtil.getSessionFactory().openSession();
//
//        List<Customer> customers = session
//                .createQuery("FROM Customer", Customer.class)
//                .list();
//
//        session.close();
//
//        return customers;
//    }
//
//    @Override
//    public List<Customer> findAll(int pageNumber, int pageSize) {
//
//        Session session = HibernateUtil.getSessionFactory().openSession();
//
//        List<Customer> customers = session
//                .createQuery("FROM Customer", Customer.class)
//                .setFirstResult(pageNumber * pageSize)
//                .setMaxResults(pageSize)
//                .list();
//
//        session.close();
//
//        return customers;
//    }
//
//    @Override
//    public Customer findByEmail(String email) {
//
//        Session session = HibernateUtil.getSessionFactory().openSession();
//
//        Customer customer = session
//                .createQuery(
//                        "FROM Customer c WHERE c.email = :email",
//                        Customer.class
//                )
//                .setParameter("email", email)
//                .uniqueResult();
//
//        session.close();
//
//        return customer;
//    }
}