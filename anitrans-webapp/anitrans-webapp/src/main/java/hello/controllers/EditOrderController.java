package hello.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.validation.Valid;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Controller
public class EditOrderController {
	@Autowired
	private hello.OrderRepository orderRepository;
	@Autowired
	private hello.UserRepository userRepository;
	//Gets the current time to check if a tour has already started. If it has in may not be edited or deleted.
	@Temporal(TemporalType.TIME)
	@DateTimeFormat(pattern = "HH:mm")
	Date currentDate = new Date();
	
	// Maps get requests for /edit-order. The id of the order to be edited needs to be passed through the URL.
    @GetMapping("/edit-order")
    public String orderForm(@RequestParam Integer id, Model model) {
    		hello.AniOrder order;
    		if(id != null) {
    			order  = orderRepository.findById(id); //finds the correct order by id.
    		} else {
    			order = new hello.AniOrder(); //if the order doesn't exist, a new one is created.
    		}
    		
    		if(new Date().after(order.getStartTime())) { //if the delivery has already started, the user is redirected to edit-order-forbidden.html.
    			return "edit-order-forbidden";
    		}
    		
    		model.addAttribute("order", order); //passes the order to edit-order.html
    		model.addAttribute("users", userRepository.findAll()); //passes all the users to edit-order.html. This is needed to select the driver.
        return "edit-order";
    }
    
    // Maps post requests for /edit-order.
    @PostMapping("/edit-order")
    public String orderSubmit(@Valid @ModelAttribute hello.AniOrder order, BindingResult bindingResult, Model model) {
    		if (bindingResult.hasErrors()) { //Checks if the edited order is still valid. If it's not, the user is sent back to correct the mistakes. Incorrect values will be marked.
    			model.addAttribute("order", order);
    			model.addAttribute("users", userRepository.findAll());
            return "edit-order";
    		}
    		
		orderRepository.save(order); //Once the order is valid, it is saved. This method won't create a new entry, but will update the old one.
		return "edit-order-success";
    }
    
    //Maps get requests for /delete-order. The id of the order to be deleted is passed through the URL.
    @GetMapping("/delete-order")
    public String deleteOrder(@RequestParam Integer id, Model model) {

    	    	hello.AniOrder order;
        	if(id != null) {
        		order  = orderRepository.findById(id); //finds the order to be deleted.
        	} else {
        		order = new hello.AniOrder(); //if it doesn't exist a new order is created.
        	}
        	if(new Date().after(order.getStartTime())) { //checks if delivery has already started. If it has the user is redirected to editorder-forbidden.html
    			return "edit-order-forbidden";
    		}
        	
        	orderRepository.delete(order); //delets the order.
    	    	return "delete-order-success";

    }
    

}


