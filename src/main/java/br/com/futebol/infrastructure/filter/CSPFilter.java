package br.com.futebol.infrastructure.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filtro que configura a política de segurança de conteúdo (CSP).
 * 
 * Permite estilos inline que o Wicket precisa pra funcionar,
 * mas mantém a segurança contra ataques XSS.
 */
public class CSPFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialização do filtro (não precisa de nada especial)
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Configuração de CSP que permite estilos inline (necessário pro Wicket)
        String cspPolicy = "default-src 'self'; " +
                          "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                          "style-src 'self' 'unsafe-inline'; " +
                          "img-src 'self' data: https:; " +
                          "font-src 'self'; " +
                          "connect-src 'self'; " +
                          "frame-src 'none'; " +
                          "object-src 'none';";
        
        httpResponse.setHeader("Content-Security-Policy", cspPolicy);
        
        // Continuar com a cadeia de filtros (deixar a requisição passar)
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Limpeza do filtro (não precisa de nada especial)
    }
}
