package com.demo.jpastreamer.service;

import com.demo.jpastreamer.persistance.*;
import com.speedment.jpastreamer.application.JPAStreamer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.demo.jpastreamer.persistance.UserDomain$.firstName;
import static com.speedment.jpastreamer.streamconfiguration.StreamConfiguration.of;
import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
public class UserService {
    //Autowire EntityManagerFactory
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final JPAStreamer jpaStreamer;
    private static final int PAGE_SIZE = 3;

    public UserDomain createUser(UserDomain userDomain) {
        return userRepository.save(userDomain);
    }
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    public List<Product> getProducts() {
        return jpaStreamer.stream(Product.class)
                .limit(3)
                .collect(Collectors.toList());
    }

    /**
     * get first 3 user domains with first name starts with A
     *
     * @return
     */
    public List<UserDomain> getUsers() {
        return jpaStreamer.stream(UserDomain.class)
                .filter(firstName.startsWith("A"))
                .sorted(firstName)
                .limit(3)
                .collect(Collectors.toList());
    }

//    Paging  (starting with page = 0)
//    filmPage(1, Film$.title.reversed());
////////////////////////////////////////////////////////////////////////// give it pages , and its constructed comparator
    public List<UserDomain> filmPage( int page, Comparator<UserDomain> comparator) {
        return jpaStreamer.stream(UserDomain.class)
                .sorted(comparator)
                .skip((long) page * PAGE_SIZE)
                .limit(PAGE_SIZE)
                .collect(Collectors.toList());
    }

    public Map<UserDomain, Product> ManytoOne (){

        Map<UserDomain, Product> languageMap = jpaStreamer.stream(of(UserDomain.class).joining(UserDomain$.language)).collect(
                        Collectors.toMap(Function.identity(),
                                UserDomain::getLanguage
                        )
                );
System.out.println(languageMap);
        return languageMap;
    }


////////////////////////////////////////////////////////////////////////// tell func what to group with and maybe the Entity
    public Map<String, List<UserDomain>> grouping (){
    Map<String, List<UserDomain>> usersByUsername = jpaStreamer.stream(of(UserDomain.class))
            .collect(groupingBy(UserDomain::getUsername));

        usersByUsername.forEach((username, userList) ->
            System.out.println("Username: " + username + ", Users: " + userList.size()));

        return usersByUsername;
    }

    @Transactional
    public String transaction(){
        //Use below code on create/update
        EntityManager em = entityManagerFactory.createEntityManager();

        try {
            em.getTransaction().begin();
            jpaStreamer.stream(UserDomain.class)
                    .filter(UserDomain$.lastName.equal("R"))
                    .forEach(f -> {
                                f.setLastName(f.getLastName() + "1");
                                em.merge(f);
                            });
                            em.getTransaction().commit();
                            return "Success";
        } catch(Exception e) {
            em.getTransaction().rollback();
            return "fail";
        }
    }


//    This filter checks whether the parameter is null. If it is, the filter returns true (meaning no filtering is applied).
//    If firstName is not null, it compares the user’s first name (retrieved using user.getFirstName()) with the provided firstName.
    public List<UserDomain> searchUsers(String firstName, String lastName, String username) {
        return jpaStreamer.stream(UserDomain.class)
                .filter(UserDomain$.firstName.equal(firstName))
                .filter(UserDomain$.lastName.equal(lastName))
                .filter(UserDomain$.username.equal(username))
                .collect(Collectors.toList());
    }

}
