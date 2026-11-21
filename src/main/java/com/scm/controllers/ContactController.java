package com.scm.controllers;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.ContactForm;
import com.scm.forms.ContactSearchForm;
import com.scm.helper.Helper;
import com.scm.helper.Message;
import com.scm.helper.MessageType;
import com.scm.services.ContactService;
import com.scm.services.UserServices;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {


    private final ContactService contactService;
    private final UserServices userServices;
    public ContactController(ContactService contactService, UserServices userServices) {
        this.contactService = contactService;
        this.userServices = userServices;
    }

    //add contact page: handler
    @GetMapping("/add")
    public String addContactView(Model model){
        ContactForm contactForm = new ContactForm();
//        contactForm.setFavorite(true);
//        contactForm.setName("Amaan Bhai");
        model.addAttribute("contactForm", contactForm);
        return "user/add_contact";
    }

    @PostMapping("/add")
    public String saveContact(@Valid @ModelAttribute ContactForm contactForm, BindingResult bindingResult, Authentication authentication, HttpSession session){
        if(bindingResult.hasErrors()){
            Message message = Message.builder().content("Contact Not Added.").type(MessageType.red).build();
            session.setAttribute("message",message);
            return "user/add_contact";
        }

        System.out.println(contactForm);
        contactService.save(contactForm, authentication);
        Message message = Message.builder().content("Contact Added Successfully.").type(MessageType.green).build();
        session.setAttribute("message",message);
        return "redirect:/user/contacts/add";
    }

    @GetMapping
    public String viewContacts(@RequestParam(value="page", defaultValue = "0") int page,
                               @RequestParam(value = "size",defaultValue = "10") int size,
                               @RequestParam(value = "sortBy",defaultValue = "name") String sortBy,
                               @RequestParam(value = "direction",defaultValue = "asc") String direction,
                               Authentication authentication, Model model){

        //new
//        String email = Helper.getEmailOfLoggedInUser(authentication);
//        User user=userServices.getUserByEmail(email);
//        Page<Contact> contactList = contactService.findByUser(user,page,size,sortBy,direction);
//        int totalPages = contactList.getTotalPages();
//        int current = contactList.getNumber(); // 0-based
//        if (totalPages > 0) {
//            // Show up to 2 pages before and after the current page
//            int start = Math.max(0, current - 2);
//            int end = Math.min(totalPages - 1, current + 2);
//
//            // Adjust bounds near the start or end to always show ~5 numbered links
//            if (current <= 2) {
//                end = Math.min(totalPages - 1, 4);
//            } else if (current >= totalPages - 3) {
//                start = Math.max(0, totalPages - 5);
//            }
//
//            List<Integer> pageNumbers = IntStream.rangeClosed(start, end)
//                    .boxed()
//                    .collect(Collectors.toList());
//
//            model.addAttribute("pageNumbers", pageNumbers);
//            model.addAttribute("showStartEllipsis", start > 1);
//            model.addAttribute("showEndEllipsis", end < totalPages - 2);
//        }





        //load all contact of a user
        String email = Helper.getEmailOfLoggedInUser(authentication);
        User user=userServices.getUserByEmail(email);
        Page<Contact> contactList = contactService.findByUser(user,page,size,sortBy,direction);
        ContactSearchForm contactSearchForm = new ContactSearchForm();
        model.addAttribute("contactSearchForm",contactSearchForm);
        model.addAttribute("contactList", contactList);
        return "user/contacts";
    }

//    Search Handler
    @GetMapping("/search")
    public String searchHandler(@ModelAttribute ContactSearchForm contactSearchForm,
                                @RequestParam(value="page", defaultValue = "0") int page,
                                @RequestParam(value = "size",defaultValue = "10") int size,
                                @RequestParam(value = "sortBy",defaultValue = "name") String sortBy,
                                @RequestParam(value = "direction",defaultValue = "asc") String direction,
                                Model model,Authentication authentication){

        String email = Helper.getEmailOfLoggedInUser(authentication);
        User user=userServices.getUserByEmail(email);

        Page<Contact> contactList=null;
        if(contactSearchForm.getField().equalsIgnoreCase("name")){
            contactList = contactService.searchByName(contactSearchForm.getKeyword(),page,size,sortBy,direction,user);
        } else if (contactSearchForm.getField().equalsIgnoreCase("email")) {
            contactList = contactService.searchByEmail(contactSearchForm.getKeyword(),page,size,sortBy,direction,user);

        } else if (contactSearchForm.getField().equalsIgnoreCase("phone")) {
            contactList = contactService.searchByPhone(contactSearchForm.getKeyword(),page,size,sortBy,direction,user);

        }
        else{

        }
        model.addAttribute("contactSearchForm",contactSearchForm);
        model.addAttribute("contactList", contactList);
        return "user/search";
    }

    @GetMapping("/delete/{contactId}")
    public String deleteContact(@PathVariable("contactId") String cId, HttpSession session){

        contactService.delete(cId);
        session.setAttribute("message", Message.builder().content("Contact is deleted successfully").type(MessageType.green).build());
        return "redirect:/user/contacts";

    }
    @GetMapping("/view/{contactId}")
    public String updateContactView(@PathVariable("contactId") String id, Model model){

        Contact contact = contactService.getById(id);

        ContactForm contactForm = new ContactForm();
        contactForm.setName(contact.getName());
        contactForm.setEmail(contact.getEmail());
        contactForm.setPhone(contact.getPhoneNumber());
        contactForm.setAddress(contact.getAddress());
        contactForm.setDescription(contact.getDescription());
        contactForm.setFavorite(contact.isFavorite());
        contactForm.setSocialLinks1(contact.getSocialLinks());
        contactForm.setLinkdin(contact.getLinkdin());
        //contactForm.setContactImage((contact.getPicture());
        model.addAttribute("image",contact.getPicture());
        model.addAttribute("contactId",contact.getId());
        model.addAttribute("contactForm", contactForm);
        return "user/update_contact";
    }

    @PostMapping("/update/{contactId}")
    public String updateContactProcess(@PathVariable String contactId,@Valid @ModelAttribute ContactForm contactForm,BindingResult bindingResult, Model model, HttpSession session){
        if(bindingResult.hasErrors()){
            Message message = Message.builder().content("Unable to update: required fields cannot be left blank.").type(MessageType.red).build();
            session.setAttribute("message",message);
            return "user/update_contact";
        }

        Contact con= contactService.update(contactForm, contactId);
        Message message = Message.builder().content("Contact Updated Successfully.").type(MessageType.green).build();

        session.setAttribute("message",message);
        return "redirect:/user/contacts/view/"+contactId;
    }
}
