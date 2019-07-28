package ru.practice.server.configuration;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

/**
 * Класс-фильтр, добавляющий CORS заголовки контроля доступа к ответу сервера
  */
@Component
public class CORSFilter implements Filter {
    /**
     * Метод, выполняющий работу фильтра
     * @param req запрос
     * @param res ответ
     * @param chain список фильтров, предназначенных для обработки
     * @throws IOException
     * @throws ServletException
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        chain.doFilter(req, res);
    }

    /**
     * Метод, определяющий конфигурационные параметры фильтра
     * @param filterConfig объект конфигурации фильтра
     */
    public void init(FilterConfig filterConfig) {}

    /**
     * Метод, вызываемый при завершении работы фильтра
     */
    public void destroy() {}
}