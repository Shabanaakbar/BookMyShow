package com.movie;

import com.movie.beans.Customer;
import com.movie.beans.MovieDetails;
import com.movie.beans.OrderHistory;
import com.movie.beans.Seat;
import com.movie.service.CustomerDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MovieController {

    @Autowired
    private CustomerDao dao;

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        String movie=(String) session.getAttribute("movieName");
        System.out.println(movie + "========Index");
        List<MovieDetails> movie2 = dao.getAllMovie();
        model.addAttribute("movieList", movie2);
        model.addAttribute("menu", "home");
        return "index";
    }

    @GetMapping("/booking")
    public String bookingCheck(@RequestParam("movieName") String movieName, Model model, HttpSession session) {
        List<MovieDetails> movieList = dao.getAllMovie();
        List<String> checkMovie = new ArrayList<>();
        for (MovieDetails movie : movieList) {
            checkMovie.add(movie.getMovieName());
        }

        if (checkMovie.contains(movieName)) {
            session.setAttribute("movieName", movieName);
            System.out.println(movieName);
			LocalDate now = LocalDate.now();
			LocalDate monthLimit = LocalDate.now();
			String time = "09:00 am";
			List<String> seatNo1 = new ArrayList<String>();
			List<Seat> all = dao.getAllSeat(now, time);

			for (Seat s : all) {
				for (String s1 : s.getSeatNo()) {
					seatNo1.add(s1);
				}

			}

			model.addAttribute("date", now);
			model.addAttribute("max", monthLimit.plusMonths(1));
			model.addAttribute("min", monthLimit);
			model.addAttribute("time", time);
			model.addAttribute("seats", seatNo1);

            return "home";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("menu", "register");
        return "register";
    }

    @GetMapping("/loginForm")
    public String loginForm(Model model) {
        model.addAttribute("menu", "login");
        return "login";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("customer") Customer customer) {
        dao.save(customer);
        return "redirect:/register";
    }

    @PostMapping("/processing")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model) {
Customer object=(Customer)session.getAttribute("user");
if(object!=null) {
	return "redirect:/booking-seat";
}else{
	Customer customer=dao.login(email, password);
	if(customer==null) {
	model.addAttribute("menu", "login");
	return "login";
	}else {
		session.setAttribute("user", customer);
	}
	
return "redirect:/home";
    }
    }
    @GetMapping("/home")
    public String mainDashboard(HttpSession session, Model model) {
session.removeAttribute("bookingdate");
session.removeAttribute("bookingtime");
session.removeAttribute("movieName");
model.addAttribute("menu", "home");
String message=(String) session.getAttribute("msg");
model.addAttribute("message", message);
session.removeAttribute("msg");
List<MovieDetails> movie2 = dao.getAllMovie();
model.addAttribute("listMovie", movie2);
    	return "main-dashboard";
    }

    @PostMapping("/booking-seat")
    public String bookSeatForm(@RequestParam("movieName") String movieName, HttpSession session, Model model) {
    	List<MovieDetails> movie2 = dao.getAllMovie();
		List<String> checkMovie = new ArrayList<>();
		for (MovieDetails string : movie2) {
			checkMovie.add(string.getMovieName());
		}
		if (checkMovie.contains(movieName)) {
			session.setAttribute("movieName", movieName);

			LocalDate now = LocalDate.now();
			LocalDate monthLimit = LocalDate.now();
			String time = "09:00 am";
			Customer customer = (Customer) session.getAttribute("user");
			List<String> seatNo1 = new ArrayList<String>();
			List<Seat> seat = customer.getSeat();

			List<Seat> all = dao.getAllSeat(now, time);

			for (Seat s : all) {
				for (String s1 : s.getSeatNo()) {
					seatNo1.add(s1);
				}
			}

			model.addAttribute("date", now);
			model.addAttribute("time", time);
			model.addAttribute("max", monthLimit.plusMonths(1));
			model.addAttribute("min", monthLimit);
			model.addAttribute("seats", seatNo1);
			model.addAttribute("seat", seat);
			session.setAttribute("user", customer);
			return "dashboard";
		} else {
			return "redirect:/home";
		}

	}

    	

    @GetMapping("/logout")
    public String logout(HttpSession session) {
    	session.removeAttribute("user");

		session.removeAttribute("bookingdate");
		session.removeAttribute("bookingtime");
		session.removeAttribute("movieName");
    	return "redirect:/";
    }

    @PostMapping("/book-seat")
    public String bookSeat(@ModelAttribute("seat") Seat seat,
                           @RequestParam("movieName") String movieName,
                           HttpSession session,
                           Model model) {
    	LocalDate currentDate = LocalDate.now();
		ZoneId defaultZoneId = ZoneId.systemDefault();
		Date todayDate = Date.from(currentDate.atStartOfDay(defaultZoneId).toInstant());
		LocalDate date = (LocalDate) session.getAttribute("bookingdate");
		String time = (String) session.getAttribute("bookingtime");
		System.out.println(seat.getSeatNo().equals(null) + " wooo" + movieName.equals(null));
		Customer object = (Customer) session.getAttribute("user");
		if (object == null) {
			return "redirect:/loginForm";
		} else if ((seat.getSeatNo().isEmpty()) && (movieName.equals(null))) {
			System.out.println("Seat is null");
			return "redirect:/home";
		} else if (date == null) {
			date = currentDate;
			time = "09:00 am";
			if (((date.isAfter(currentDate)) || (date.equals(currentDate)))
					&& (date.isBefore(currentDate.plusMonths(1)) || date.equals(currentDate.plusMonths(1)))) {

				Date date2 = Date.from(date.atStartOfDay(defaultZoneId).toInstant());
				List<Double> price = new ArrayList<Double>();
				double sum = 0;
				double p = 525.22d;
				for (String s : seat.getSeatNo()) {
					sum = sum + p;
					price.add(p);
    }
				seat.setTotal(sum);
				seat.setPrice(price);
				OrderHistory history = new OrderHistory(seat.getSeatNo(), price, sum, movieName, todayDate, date2, time,
						object);
				dao.saveSeat(seat, object, date2, time);
				dao.saveHistory(history, object);
				List<String> seatNo1 = new ArrayList<String>();
				List<Customer> all = dao.getAll();
				for (Customer c : all) {
					for (Seat s : c.getSeat()) {
						for (String s1 : s.getSeatNo()) {
							seatNo1.add(s1);
						}

					}
				
				}
				model.addAttribute("seats", seatNo1);
				session.setAttribute("user", object);
				session.setAttribute("msg", "your seat book successsfully");
				return "redirect:/home";

			} else {
				System.out.println("ye date current date se pahle ki date hai");
				return "redirect:/booking-seat?movieName=" + movieName;

			}
		} else {
			if (((date.isAfter(currentDate)) || (date.equals(currentDate)))
					&& (date.isBefore(currentDate.plusMonths(1)) || date.equals(currentDate.plusMonths(1)))) {
				Date date2 = Date.from(date.atStartOfDay(defaultZoneId).toInstant());
				List<Double> price = new ArrayList<Double>();
				double sum = 0;
				double p = 525.22d;
				for (String s : seat.getSeatNo()) {
					sum = sum + p;
					price.add(p);
				}
				seat.setTotal(sum);
				seat.setPrice(price);
				OrderHistory history = new OrderHistory(seat.getSeatNo(), price, sum, movieName, todayDate, date2, time,
						object);
				dao.saveSeat(seat, object, date2, time);
				dao.saveHistory(history, object);
				List<String> seatNo1 = new ArrayList<String>();
				List<Customer> all = dao.getAll();
				for (Customer c : all) {
					for (Seat s : c.getSeat()) {
						for (String s1 : s.getSeatNo()) {
							seatNo1.add(s1);
						}

					}
				}
				model.addAttribute("seats", seatNo1);
				session.setAttribute("user", object);
				session.setAttribute("msg", "your seat book successsfully");
				return "redirect:/home";

			} else {
				System.out.println("ye date current date se pahle ki date hai");
				return "redirect:/booking-seat?movieName=" + movieName;
			}
		}
    }
				@GetMapping("/order-history")
    public String history(HttpSession session, Model model) {
					Date todayDate = new Date();
					Customer object = (Customer) session.getAttribute("user");
					session.setAttribute("user", object);
					List<OrderHistory> list = dao.getAllHistory(object.getBid());
					model.addAttribute("hList", list);
					model.addAttribute("todaydate", todayDate);
					LocalDate date = (LocalDate) session.getAttribute("bookingdate");
					System.out.println(date);
					model.addAttribute("menu", "order");
        return "history";
    }

    @GetMapping("/clear-seats")
    public String eraseSeat(HttpSession session) {
    	LocalDate now = LocalDate.now();
		String time = "09:00 am";
		Customer object = (Customer) session.getAttribute("user");

		if (object != null) {
			List<Seat> list = dao.getAllSeat(now, time);
			for (Seat seat : list) {
				long id = seat.getsId();
				dao.delete(id);
			}

		}
    	return "redirect:/booking-seat";
    }

    @GetMapping("/all-customers-records")
    public String allRecords(Model model, HttpSession session) {
    	Customer object = (Customer) session.getAttribute("user");
		long bid = object.getBid();
		if (bid == 1) {
			List<Customer> all = dao.getAll();
			model.addAttribute("records", all);
			model.addAttribute("menu", "allusers");
			return "user_records";
    }else {
		return "redirect:/booking-seat";
	}
}

    @GetMapping("/all-seats/{id}")
    public String allSeats(@PathVariable("id") long id, Model model, HttpSession session) {
    	Customer object = (Customer) session.getAttribute("user");
		long bid = object.getBid();
		if (bid == 1) {
			List<OrderHistory> list = dao.getAllHistory(id);
			model.addAttribute("seatRecords", list);
			model.addAttribute("menu", "allusers");
    	return "seat-records";
    }else {
		return "redirect:/booking-seat";
	}

}

    @GetMapping("/setting")
    public String getSetting(Model model, HttpSession session) {
    	Customer customer = (Customer) session.getAttribute("user");
		model.addAttribute("user", customer);
		model.addAttribute("menu", "setting");
    	return "setting";
    }

    @GetMapping("/setting/update/{id}")
    public String updateForm(@PathVariable("id") long id, Model model) {
    	System.out.println(id);
		model.addAttribute("menu", "setting");        
		return "update-details";
    }

    @PostMapping("/setting/update-details")
    public String updateDetails(@ModelAttribute("customer") Customer cust, HttpSession session) {
    	String name = cust.getName();
		String email = cust.getEmail();
		Customer customer = (Customer) session.getAttribute("user");
		customer.setName(name);
		customer.setEmail(email);
		dao.updateDetail(customer);
    	return "redirect:/setting";
    }

    @PostMapping("/check")
    public String checkDate(@RequestParam("localdate") String date,
                            @RequestParam("localtime") String time,
                            Model model,
                            HttpSession session) {
    	Customer object = (Customer) session.getAttribute("user");
		String movie = (String) session.getAttribute("movieName");
		LocalDate monthLimit = LocalDate.now();
		if (movie.equals(null)) {
			return "home";

		} else if (object == null) {
			LocalDate now = LocalDate.parse(date);
			List<String> seatNo1 = new ArrayList<String>();
			List<Seat> all = dao.getAllSeat(now, time);

			for (Seat s : all) {
				for (String s1 : s.getSeatNo()) {
					seatNo1.add(s1);
				}

			}
			session.setAttribute("bookingdate", now);
			session.setAttribute("bookingtime", time);
			model.addAttribute("date", now);
			model.addAttribute("max", monthLimit.plusMonths(1));
			model.addAttribute("min", monthLimit);
			model.addAttribute("time", time);
			model.addAttribute("seats", seatNo1);

			return "home";
		} else{
			LocalDate now = LocalDate.parse(date);
			List<String> seatNo1 = new ArrayList<String>();
			List<Seat> all = dao.getAllSeat(now, time);

			for (Seat s : all) {
				for (String s1 : s.getSeatNo()) {
					seatNo1.add(s1);
				}

			}
			session.setAttribute("bookingdate", now);
			session.setAttribute("bookingtime", time);
			model.addAttribute("date", now);
			model.addAttribute("max", monthLimit.plusMonths(1));
			model.addAttribute("min", monthLimit);
			model.addAttribute("time", time);
			model.addAttribute("seats", seatNo1);

			return "dashboard";
		}

	}

    @ExceptionHandler(Exception.class)
    public String handleError(Exception ex, Model model, HttpSession session) {
    	Customer object = (Customer) session.getAttribute("user");
		if (object == null) {
			return "redirect:/loginForm";
		} else {
			return "redirect:/home";
		}    }
}
