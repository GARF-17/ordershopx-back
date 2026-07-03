package com.ordershopx.backend.modules.onboarding.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActivacionPageController {

    @GetMapping(value = "/activar", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> mostrarTokenDeActivacion(@RequestParam String token) {
        String html = """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Activación OrderShopX</title>
                <style>
                    body {
                        font-family: -apple-system, sans-serif;
                        background: #100A24;
                        color: #fff;
                        display: flex;
                        flex-direction: column;
                        align-items: center;
                        justify-content: center;
                        min-height: 100vh;
                        margin: 0;
                        padding: 20px;
                        text-align: center;
                    }
                    h1 { color: #8B5CF6; font-size: 22px; }
                    .token-box {
                        background: #1a1330;
                        border: 1px solid #8B5CF6;
                        border-radius: 8px;
                        padding: 16px;
                        margin: 20px 0;
                        word-break: break-all;
                        font-family: monospace;
                        font-size: 13px;
                        max-width: 500px;
                    }
                    button {
                        background: #8B5CF6;
                        color: #fff;
                        border: none;
                        padding: 12px 24px;
                        border-radius: 6px;
                        font-size: 16px;
                        cursor: pointer;
                    }
                    p { color: #ccc; font-size: 14px; max-width: 400px; }
                </style>
            </head>
            <body>
                <h1>🔑 Activación de tu cuenta OrderShopX</h1>
                <p>Copia el siguiente token y pégalo en la app para continuar con la activación de tu restaurante.</p>
                <div class="token-box" id="tokenBox">%s</div>
                <button onclick="copiarToken()">Copiar token</button>
                <p id="mensaje"></p>
                <script>
                    function copiarToken() {
                        const texto = document.getElementById('tokenBox').innerText;
                        navigator.clipboard.writeText(texto).then(() => {
                            document.getElementById('mensaje').innerText = '✅ Token copiado';
                        });
                    }
                </script>
            </body>
            </html>
            """.formatted(token);

        return ResponseEntity.ok(html);
    }
}