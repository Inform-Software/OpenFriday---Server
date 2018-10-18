package com.syncrotess.openfriday.core;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//Currently not functional
@ControllerAdvice
public class ErrorHandler {

  @ExceptionHandler (value = NoHandlerFoundException.class)
  public Object handleStaticResourceNotFound (final NoHandlerFoundException ex,
                                              HttpServletRequest req,
                                              RedirectAttributes redirectAttributes) {
    if (req.getRequestURI ().startsWith ("/web")) {
      return "redirect:/web/index.html";
    } else {
      redirectAttributes.addFlashAttribute ("errorMessage", "The requested site was not found!");
      return "redirect:/web/index.html";
    }
  }
}
