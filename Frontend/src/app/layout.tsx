import React from "react";
import Image from "next/image";
import img1 from "../assets/hugohub_logo.jpeg";
import "./globals.css";

export default function RootLayout({
  children,
}: Readonly<{ children: React.ReactNode }>) {
  return (
    <html>
      <body>
        <header className="header">
          <div className="logo">
            <Image src={img1} alt="Company Logo" width={50} height={50} />
          </div>
          <div className="companyName">HugoHub</div>
        </header>
        <main>{children}</main>
      </body>
    </html>
  );
}
