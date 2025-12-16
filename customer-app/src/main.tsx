import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import App from "./App";

import "@mantine/core/styles.css";
import "@mantine/notifications/styles.css";
import { MantineProvider } from "@mantine/core";
import { Notifications } from "@mantine/notifications";

import { WSProvider } from "./api/providers/WSProvider";

ReactDOM.createRoot(document.getElementById("root")!).render(
  // <React.StrictMode>
    <BrowserRouter>
      <MantineProvider>
        <Notifications />

        {/* 🔥 WebSocket Provider는 App을 감싸야 한다 */}
        <WSProvider>
          <App />
        </WSProvider>

      </MantineProvider>
    </BrowserRouter>
  /* </React.StrictMode> */
);
