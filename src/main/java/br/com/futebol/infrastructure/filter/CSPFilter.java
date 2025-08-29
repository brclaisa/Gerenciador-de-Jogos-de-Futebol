package br.com.futebol.infrastructure.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filtro para configurar Content Security Policy (CSP)
 * Permite estilos inline necessários para o Wicket
 */
public class CSPFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialização do filtro
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Configuração de CSP que permite estilos inline
        String cspPolicy = "default-src 'self'; " +
                          "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                          "style-src 'self' 'unsafe-inline'; " +
                          "img-src 'self' data: https:; " +
                          "font-src 'self'; " +
                          "connect-src 'self'; " +
                          "frame-src 'none'; " +
                          "object-src 'none';";
        
        httpResponse.setHeader("Content-Security-Policy", cspPolicy);
        
        // Continuar com a cadeia de filtros
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Limpeza do filtro
    }
}
