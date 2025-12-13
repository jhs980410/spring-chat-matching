// main.tsx
import React from "react";
import ReactDOM from "react-dom/client";
import { MantineProvider } from "@mantine/core";
import { Notifications } from "@mantine/notifications";
import { BrowserRouter } from "react-router-dom";

import "@mantine/core/styles.css";
import "@mantine/notifications/styles.css";
import "@mantine/charts/styles.css";

import WebSocketRoot from "./features/providers/WebSocketRoot";

ReactDOM.createRoot(document.getElementById("root")!).render(
  <MantineProvider defaultColorScheme="light">
    <Notifications position="top-right" />
    <BrowserRouter>
      <WebSocketRoot />
    </BrowserRouter>
  </MantineProvider>
);
