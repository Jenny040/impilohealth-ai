/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,jsx,ts,tsx}"],
  theme: {
    extend: {
      colors: {
        background: "#FFF5F9", foreground: "#251728", primary: "#9C27B0",
        accent: "#E91E8C", secondary: "#F7E4F4", muted: "#756777", border: "#EEE2E9",
        "chart-1": "#9C27B0", "chart-2": "#E91E8C", "chart-3": "#FF6FB5",
        "chart-4": "#E7A52A", "chart-5": "#41A4CF", sidebar: "#FFFFFF",
        "sidebar-foreground": "#251728", "sidebar-primary": "#9C27B0",
        "sidebar-accent": "#F7E4F4", "sidebar-border": "#EEE2E9",
      },
      fontFamily: { sans: ["Poppins", "sans-serif"] },
      borderRadius: { card: "16px", panel: "24px" },
      boxShadow: { soft: "0 12px 32px rgba(156,39,176,.09)", card: "0 18px 42px rgba(111,25,127,.12)" },
    },
  },
};
