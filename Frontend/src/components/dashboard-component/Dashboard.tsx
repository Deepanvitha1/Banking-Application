"use client";

import React, { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";
import { getCookie } from "cookies-next";
import "@/components/dashboard-component/Dashboard.css";
import { UserDetails } from "@/components/types/UserDetailsTypes";
import { UserAccountDetails } from "@/components/types/AccountTypes";

export default function Dashboard() {
  const [message, setMessage] = useState<string | null>(null);
  const [userDetails, setUserDetails] = useState<UserDetails | null>(null);
  const [userAccountDetails, setUserAccountDetails] = useState<
    UserAccountDetails[]
  >([]);
  const router = useRouter();

  const token = getCookie("token");
  const userId = getCookie("userId");

  useEffect(() => {
    if (!token || !userId) {
      router.push("/home");
    }
  }, [router, token, userId]);

  useEffect(() => {
    if (!token || !userId) return;

    const fetchUserDetails = async () => {
      try {
        const res = axios.get("/api/get-user-details");

        if (!(await res).data.success) {
          throw new Error("Failed to fetch user details");
        }

        setUserDetails((await res).data.data.data);
      } catch (error) {
        setMessage(
          `Error fetching user details: ${
            error instanceof Error ? error.message : "Unknown error"
          }`
        );
      }
    };

    fetchUserDetails();
  }, [token, userId]);

  useEffect(() => {
    if (!token || !userId) return;

    const fetchUserAccounts = async () => {
      try {
        const res = await axios.get<{
          success: boolean;
          data: { data: { accounts: UserAccountDetails[] } };
        }>("/api/get-accounts", {
          headers: { Authorization: `Bearer ${token}` },
          params: { userId },
        });

        if (!res.data.success) {
          throw new Error("Failed to fetch user accounts");
        }

        setUserAccountDetails(res.data.data.data.accounts || []);
      } catch (error) {
        setMessage(
          `Error fetching user accounts: ${
            error instanceof Error ? error.message : "Unknown error"
          }`
        );
      }
    };

    fetchUserAccounts();
  }, [token, userId]);

  const totalBalance = userAccountDetails.reduce(
    (sum, account) => sum + (account.balance || 0),
    0
  );

  return (
    <div className="parentDashboard">
      <div className="dashboard">
        <header className="dashboard-header">
          <h1>Welcome, {userDetails?.first_name || "User"}</h1>
        </header>
        {message && <p className="error-message">{message}</p>}
        <div className="emptySection">
          <div className="accounts-header">
            <h2>Your Accounts</h2>
            <div className="total-balance">
              <strong>Total Balance:</strong> ₹{totalBalance.toFixed(2)}
            </div>
          </div>
          {userAccountDetails.length > 0 ? (
            <table className="accounts-table">
              <thead>
                <tr>
                  <th>Account ID</th>
                  <th>Balance</th>
                  <th>Account Type</th>
                </tr>
              </thead>
              <tbody>
                {userAccountDetails.map((account) => (
                  <tr key={account.accountId}>
                    <td>{account.accountId}</td>
                    <td>₹{account.balance?.toFixed(2)}</td>
                    <td>
                      {account.type === "savings" ? "Savings" : "Current"}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <p>No accounts found</p>
          )}
        </div>
        <section className="profile-section">
          <h2>Profile</h2>
          {userDetails ? (
            <div className="profile-details">
              <p>
                <strong>Name:</strong> {userDetails.first_name}{" "}
                {userDetails.last_name}
              </p>
              <p>
                <strong>Phone Number:</strong>{" "}
                {userDetails.phone_number || "N/A"}
              </p>
              <p>
                <strong>Email:</strong> {userDetails.email || "N/A"}
              </p>
              <p>
                <strong>Date of Birth:</strong> {userDetails.dob || "N/A"}
              </p>
              <p>
                <strong>Address:</strong> {userDetails.address || "N/A"}
              </p>
            </div>
          ) : (
            <p>Loading profile...</p>
          )}
        </section>
      </div>
    </div>
  );
}
