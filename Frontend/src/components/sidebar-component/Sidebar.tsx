"use client";
import React from "react";
import { useRouter } from "next/navigation";
import "@/components/sidebar-component/Sidebar.css";
import axios from "axios";

const Sidebar = () => {
  const router = useRouter();

  const handleLogout = async () => {
    try {
      const res = await axios.post("/api/logout");
      if (res.data.message === "Logged out") router.push("/home");
    } catch (error) {
      console.error("Logout Failed: ", error);
    }
  };

  const handleNavigation = (path: string) => {
    router.push(path);
  };

  return (
    <div className="sidebar">
      <ul>
        <li
          className="side-group"
          onClick={() => handleNavigation("/dashboard")}
        >
          Dashboard
        </li>
        <li
          className="side-group"
          onClick={() => handleNavigation("/dashboard/accounts")}
        >
          Accounts
        </li>
        <li
          className="side-group"
          onClick={() => handleNavigation("/dashboard/payments")}
        >
          Payments
        </li>
        <li
          className="side-group"
          onClick={() => handleNavigation("/dashboard/transactions")}
        >
          Transactions
        </li>
        <li className="side-group logout" onClick={handleLogout}>
          Logout
        </li>
      </ul>
    </div>
  );
};

export default Sidebar;
