package CartApi.Cart;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByProductContaining(String product);
    List<CartItem> findByPriceLessThanEqual(Double maxPrice);
    List<CartItem> findByProductStartingWith(String prefix);
    List<CartItem> findByProductContainingAndPriceLessThanEqual(String product, Double maxPrice);
    List<CartItem> findByProductContainingAndProductStartingWith(String product, String prefix);
    List<CartItem> findByPriceLessThanEqualAndProductStartingWith(Double maxPrice, String prefix);
    List<CartItem> findByProductContainingAndPriceLessThanEqualAndProductStartingWith(String product, Double maxPrice, String prefix);
    
    List<CartItem> findByProductContaining(String product, Pageable pageable);
    List<CartItem> findByPriceLessThanEqual(Double maxPrice, Pageable pageable);
    List<CartItem> findByProductStartingWith(String prefix, Pageable pageable);
    List<CartItem> findByProductContainingAndPriceLessThanEqual(String product, Double maxPrice, Pageable pageable);
    List<CartItem> findByProductContainingAndProductStartingWith(String product, String prefix, Pageable pageable);
    List<CartItem> findByPriceLessThanEqualAndProductStartingWith(Double maxPrice, String prefix, Pageable pageable);
    List<CartItem> findByProductContainingAndPriceLessThanEqualAndProductStartingWith(String product, Double maxPrice, String prefix, Pageable pageable);
}
