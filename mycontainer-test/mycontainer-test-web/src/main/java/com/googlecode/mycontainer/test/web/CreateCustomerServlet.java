/*
 * Copyright 2008 Whohoo Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable
 * law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package com.googlecode.mycontainer.test.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.mycontainer.test.CustomerService;
import com.googlecode.mycontainer.test.ejb.CustomerBean;

public class CreateCustomerServlet extends HttpServlet {

    private static final long serialVersionUID = -5214416292942723529L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        CustomerBean customerBean = new CustomerBean();
        customerBean.setName(name);

        try {
            InitialContext ic = new InitialContext();
            CustomerService service = (CustomerService) ic.lookup("CustomerServiceBean/local");

            CustomerBean createCustomer = service.createCustomer(customerBean);
            PrintWriter out = resp.getWriter();
            out.println("<html><body>");
            out.print("Success! The Customer ID is " + createCustomer.getId() + "<br/>");
            out.println("<a href=\"index.html\">back</a>");
            out.println("</body></html>");

        } catch (NamingException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

}
