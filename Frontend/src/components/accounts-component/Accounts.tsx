"use client";
import React, { useEffect, useState } from "react";
import "@/components/accounts-component/Accounts.css";
import { useRouter } from "next/navigation";
import axios, { AxiosError } from "axios";
import { getCookie } from "cookies-next";
import { UserAccountDetails } from "@/components/types/AccountTypes";

function Accounts() {
  const [userAccountDetails, setUserAccountDetails] = useState<
    UserAccountDetails[]
  >([]);
  const [showDropdown, setShowDropdown] = useState(false);
  const [, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const router = useRouter();
  const userId = getCookie("userId");
  const token = getCookie("token");

  useEffect(() => {
    if (!token) {
      router.push("/login");
    }
  }, [router, token]);
  useEffect(() => {
    const fetchUserAccounts = async () => {
      try {
        if (!token || !userId) {
          throw new Error("Unauthorized: Missing token or userId.");
        }

        const response = await axios.get("/api/get-accounts", {
          headers: { Authorization: `Bearer ${token}` },
          params: { userId },
        });

        if (!response.data.success) {
          throw new Error(response.data.data || "Failed to fetch accounts");
        }

        if (response.data && response.data.data.data.accounts) {
          setUserAccountDetails(response.data.data.data.accounts);
        } else {
          setUserAccountDetails([]);
        }
      } catch (error) {
        setMessage("Error fetching accounts:" + error);
        setUserAccountDetails([]);
      }
    };
    fetchUserAccounts();
  }, [token, userId]);

  const handleCreateAccount = async (accountType: string) => {
    setLoading(true);
    setMessage("");
    setShowDropdown(false);

    try {
      const res = await axios.post(
        "/api/create-account",
        {
          type_of_account: accountType,
        },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      if (res.data.success) {
        const accountData = res.data.data.data;
        setMessage("Account Created Successfully!");
        setUserAccountDetails([
          ...userAccountDetails,
          {
            accountId: accountData.accountId,
            balance: accountData.balance,
            type: accountData.type_of_account,
          },
        ]);
      } else {
        setMessage(res.data.error || "Failed to create account.");
      }
    } catch (error: unknown) {
      if (axios.isAxiosError(error)) {
        const axiosError = error as AxiosError<{
          error?: string;
          message?: string;
        }>;

        if (axiosError.response?.data?.error) {
          setMessage(
            typeof axiosError.response.data.error === "string"
              ? axiosError.response.data.error
              : axiosError.response.data.message || "An error occurred"
          );
        } else {
          setMessage("An error occurred while processing the request.");
        }
      } else {
        setMessage("An unexpected error occurred.");
      }
    }
  };
  return (
    <div className="accounts-container">
      <section className="accounts-section">
        <div className="accounts-header">
          <h2>Your Accounts</h2>
          <div className="create-account-dropdown">
            <button
              className="create-button"
              onClick={() => setShowDropdown(!showDropdown)}
            >
              + Create Account
            </button>
            {showDropdown && (
              <div className="dropdown-menu">
                <button onClick={() => handleCreateAccount("savings")}>
                  Savings
                </button>
                <button onClick={() => handleCreateAccount("current")}>
                  Current
                </button>
              </div>
            )}
          </div>
        </div>

        <div className="accounts-list">
          {userAccountDetails.length > 0 ? (
            userAccountDetails.map((account) => (
              <div className="account-card" key={account.accountId}>
                <h3>
                  {account?.type === "savings" ? "Savings" : "Current"} Account
                </h3>

                <p>Account Number: {account.accountId}</p>
                <p>Balance: ₹{account.balance?.toFixed(2)}</p>
              </div>
            ))
          ) : (
            <p>No accounts found.</p>
          )}
        </div>
        {message && <p className="message">{message}</p>}
      </section>
      <div className="account-info">
        <p>
          <strong>Savings Account:</strong> Ideal for individuals to save money
          while earning interest. It has limited transactions but ensures
          financial security and growth.
        </p>
        <p>
          <strong>Current Account:</strong> Designed for businesses and frequent
          transactions. It offers no interest but provides higher withdrawal
          limits and seamless fund management.
        </p>
      </div>
    </div>
  );
}

export default Accounts;
