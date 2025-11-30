// auto-script.js
(function () {
    const TOKEN_KEY = "swagger_jwt_token";

    function getToken() {
        return window.localStorage.getItem(TOKEN_KEY) || "";
    }

    function setToken(token) {
        window.localStorage.setItem(TOKEN_KEY, token);
    }

    function initSwaggerUI() {
        const storedToken = getToken();
        const input = document.getElementById("jwtTokenInput");
        if (input && storedToken) {
            input.value = storedToken;
        }

        // Swagger UI 초기화
        window.ui = SwaggerUIBundle({
            url: "/v3/api-docs",
            dom_id: "#swagger-ui",
            deepLinking: true,
            presets: [
                SwaggerUIBundle.presets.apis,
                SwaggerUIStandalonePreset
            ],
            layout: "BaseLayout",

            // 모든 API 요청 → 자동 Authorization 삽입
            requestInterceptor: function (req) {
                const token = getToken();
                if (token) {
                    req.headers["Authorization"] = "Bearer " + token;
                }
                return req;
            }
        });

        // BearerAuth 이름으로 preauthorize 시도
        if (storedToken && window.ui.preauthorizeApiKey) {
            try {
                window.ui.preauthorizeApiKey("BearerAuth", "Bearer " + storedToken);
            } catch (e) {
                console.warn("preauthorizeApiKey 실패(무시 가능):", e);
            }
        }
    }

    window.addEventListener("load", function () {
        // 상단 입력 → 토큰 저장 버튼
        const btn = document.getElementById("applyJwtBtn");
        if (btn) {
            btn.addEventListener("click", function () {
                const input = document.getElementById("jwtTokenInput");
                const raw = (input?.value || "").trim();
                if (!raw) {
                    alert("토큰이 비어 있음.");
                    return;
                }
                setToken(raw);
                alert("JWT 토큰 저장 완료.\nSwagger 모든 요청에 자동 적용됩니다.");
            });
        }

        // Swagger UI 렌더링
        initSwaggerUI();
    });
})();
