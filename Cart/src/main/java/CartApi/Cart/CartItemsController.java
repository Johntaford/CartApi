package CartApi.Cart;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@CrossOrigin
@RestController
public class CartItemsController {

	private CartItemRepository repository;

	@Autowired
    public CartItemsController(CartItemRepository repository) {
		this.repository = repository;
	} 
   
	    
	    @GetMapping("/cart-items")
	    public List<CartItem> getAllCartItems(
	        @RequestParam(required = false) String product,
	        @RequestParam(required = false) Double maxPrice,
	        @RequestParam(required = false) String prefix,
	        @RequestParam(required = false) Integer pageSize) {
	    	
	    	if (pageSize != null) {
	            Pageable paging = PageRequest.of(0, pageSize);
	            if (product != null && maxPrice != null && prefix != null) {
	                return repository.findByProductContainingAndPriceLessThanEqualAndProductStartingWith(
	                    product, maxPrice, prefix, paging);
	            } else if (product != null && maxPrice != null) {
	                return repository.findByProductContainingAndPriceLessThanEqual(
	                    product, maxPrice, paging);
	            } else if (product != null && prefix != null) {
	                return repository.findByProductContainingAndProductStartingWith(product, prefix, paging);
	            } else if (maxPrice != null && prefix != null) {
	                return repository.findByPriceLessThanEqualAndProductStartingWith(maxPrice, prefix, paging);
	            } else if (product != null) {
	                return repository.findByProductContaining(product, paging);
	            } else if (maxPrice != null) {
	                return repository.findByPriceLessThanEqual(maxPrice, paging);
	            } else if (prefix != null) {
	                return repository.findByProductStartingWith(prefix, paging);
	            }
	            return repository.findAll(paging).getContent();
	        } else {
	            if (product != null && maxPrice != null && prefix != null) {
	                return repository.findByProductContainingAndPriceLessThanEqualAndProductStartingWith(
	                    product, maxPrice, prefix);
	            } else if (product != null && maxPrice != null) {
	                return repository.findByProductContainingAndPriceLessThanEqual(
	                    product, maxPrice);
	            } else if (product != null && prefix != null) {
	                return repository.findByProductContainingAndProductStartingWith(product, prefix);
	            } else if (maxPrice != null && prefix != null) {
	                return repository.findByPriceLessThanEqualAndProductStartingWith(maxPrice, prefix);
	            } else if (product != null) {
	                return repository.findByProductContaining(product);
	            } else if (maxPrice != null) {
	                return repository.findByPriceLessThanEqual(maxPrice);
	            } else if (prefix != null) {
	                return repository.findByProductStartingWith(prefix);
	            }
	            return repository.findAll();
	        }
	    
	    }
	    
	    
	    @GetMapping("/cart-items/{id}")
	    public ResponseEntity<CartItem> getCartItemById(@PathVariable(value = "id") Long id, Pageable pageable) {
	        Optional<CartItem> cartItem = repository.findById(id);
	        return cartItem.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	    }
	    
	    @PostMapping("/cart-items")
	    public ResponseEntity<CartItem> addCartItem(@Validated @RequestBody CartItem cartItem) {
	        CartItem newCartItem = repository.save(cartItem);
	        return ResponseEntity.created(
	                ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
	                        .buildAndExpand(newCartItem.getId()).toUri()).body(newCartItem);
	    }
	    
	    @PutMapping("/{id}")
	    public ResponseEntity<CartItem> updateCartItem(@PathVariable(value = "id") Long id, @Validated @RequestBody CartItem cartItem) {
	        Optional<CartItem> optionalCartItem = repository.findById(id);

	        if (optionalCartItem.isPresent()) {
	            CartItem existingCartItem = optionalCartItem.get();
	            existingCartItem.setProduct(cartItem.getProduct());
	            existingCartItem.setPrice(cartItem.getPrice());
	            existingCartItem.setQuantity(cartItem.getQuantity());

	            CartItem updatedCartItem = repository.save(existingCartItem);
	            return ResponseEntity.ok(updatedCartItem);
	        } else {
	            return ResponseEntity.notFound().build();
	        }
	    }
	    
	    
	    @DeleteMapping("/{id}")
	    public ResponseEntity<Void> deleteCartItem(@PathVariable(value = "id") Long id) {
	        repository.deleteById(id);
	        return ResponseEntity.noContent().build();
	    }
	    
	    @GetMapping("/total-cost")
	    public Double getTotalCost() {
	        List<CartItem> cartItems = repository.findAll();
	        Double totalCost = cartItems.stream()
	                .mapToDouble(cartItem -> cartItem.getPrice() * cartItem.getQuantity())
	                .sum();

	        return totalCost * 1.06;
	    }
	    
	    @PatchMapping("/cart-items/{id}/add")
	    public CartItem addQuantity(@PathVariable Long id, @RequestParam int count) {
	        Optional<CartItem> optionalCartItem = repository.findById(id);
	        if (optionalCartItem.isPresent()) {
	            CartItem cartItem = optionalCartItem.get();
	            cartItem.setQuantity(cartItem.getQuantity() + count);
	            return repository.save(cartItem);
	        } else {
	            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID Not Found");
	        }
	    }
	    //Tried to implement this but the local is showing null idk why 
	    @PostMapping("/cart-items/init")
	    public void initCartItems() {
	        List<CartItem> cartItems = new ArrayList<>();
	        cartItems.add(new CartItem("Cups", 10.00, 50));
	        cartItems.add(new CartItem("Plates", 8.75, 50));
	        cartItems.add(new CartItem("Silverware", 5.99, 200));
	        cartItems.add(new CartItem("Table", 99.99, 3));
	        cartItems.add(new CartItem("Party Hats", 2.50, 100));
	        cartItems.add(new CartItem("Balloons", 1.00, 75));
	        cartItems.add(new CartItem("Food", 175.31, 6));
	        cartItems.add(new CartItem("Drinks", 83.79, 200));
	        repository.saveAll(cartItems);
	    }

}
