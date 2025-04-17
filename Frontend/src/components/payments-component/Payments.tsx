"use client";
import React, { useState, useEffect } from "react";
import axios from "axios";
import "@/components/payments-component/Payments.css";
import { useRouter } from "next/navigation";
import { getCookie } from "cookies-next";
import { Accounts } from "@/components/types/AccountTypes";

function Payments() {
  const [selectedBank, setSelectedBank] = useState("");
  const [sourceAccount, setSourceAccount] = useState("");
  const [recipientAccount, setRecipientAccount] = useState("");
  const [amount, setAmount] = useState("");
  const [transferNote, setTransferNote] = useState("");
  const [userAccountDetails, setUserAccountDetails] = useState<Accounts[]>([]);
  const [error, setError] = useState<string>(""); // State for error messages
  const [successMessage, setSuccessMessage] = useState<string>(""); // State for success messages
  const router = useRouter();

  const token = getCookie("token") || "";
  const userId = getCookie("userId") || "";

  useEffect(() => {
    if (!token) {
      router.push("/login");
    }
  }, [token, router]);

  useEffect(() => {
    if (!token || !userId) return;

    const fetchUserAccounts = async () => {
      try {
        const res = await axios.get("/api/get-accounts", {
          headers: { Authorization: `Bearer ${token}` },
          params: { userId },
        });
        setUserAccountDetails(res.data.data.data?.accounts);
      } catch (error) {
        setError("Error fetching user accounts: " + error);
      }
    };

    fetchUserAccounts();
  }, [token, userId]);

  useEffect(() => {
    if (selectedBank && userAccountDetails?.length > 0) {
      const selectedBankType = selectedBank.toLowerCase();
      const selectedAccount = userAccountDetails.find(
        (acc) => acc.type?.toLowerCase() === selectedBankType
      );

      setSourceAccount(selectedAccount?.accountId ?? "");
    } else {
      setSourceAccount("");
    }
  }, [selectedBank, userAccountDetails]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!sourceAccount.trim() || !recipientAccount.trim() || !amount.trim()) {
      setError("Please fill in all required fields.");
      setSuccessMessage("");

      setTimeout(() => {
        setError("");
        setSuccessMessage("");
      }, 3000);
      return;
    }

    const paymentData = {
      senderAccountId: sourceAccount,
      receiverAccountId: recipientAccount,
      transactionalNote: transferNote || "",
      amount: parseFloat(amount),
    };

    try {
      const response = await axios.post("/api/send-money", paymentData, {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (response.data && response.data.success) {
        setSuccessMessage("Transaction Successful!");
        setError("");
      } else {
        setError(
          `Payment failed: ${
            response.data.data?.headers?.message || "Unknown error"
          }`
        );
        setSuccessMessage("");
      }
    } catch (error: unknown) {
      if (axios.isAxiosError(error)) {
        setError(
          `Payment failed: ${
            error.response?.data?.error || "An unexpected error occurred"
          }`
        );
      } else {
        setError("Payment failed: An unexpected error occurred");
      }
      setSuccessMessage("");
    }
    setTimeout(() => {
      setError("");
      setSuccessMessage("");
    }, 3000);
  };

  return (
    <div className="payments-container">
      <form className="payments-form" onSubmit={handleSubmit}>
        <section className="form-section">
          <header className="payments-header">
            <h1>Payment Transfer</h1>
          </header>
          <h3>Transfer Details</h3>
          <div className="form-group">
            <label htmlFor="sourceBank">Select Source Bank</label>
            <select
              id="sourceBank"
              className="input-field"
              value={selectedBank}
              onChange={(e) => setSelectedBank(e.target.value)}
            >
              <option value="">Select Bank</option>
              <option value="savings">Savings Account</option>
              <option value="current">Current Account</option>
            </select>
          </div>
          <div className="form-group">
            <label htmlFor="sourceAccount">Source Account Number</label>
            <input
              type="text"
              id="sourceAccount"
              className="input-field"
              value={sourceAccount || "No account found"}
              disabled
            />
          </div>
          <div className="form-group">
            <label htmlFor="transferNote">Transfer Note (Optional)</label>
            <textarea
              id="transferNote"
              placeholder="Any additional information or instructions"
              className="input-field"
              value={transferNote}
              onChange={(e) => setTransferNote(e.target.value)}
            ></textarea>
          </div>
        </section>
        <section className="form-section">
          <h3>Bank Account Details</h3>
          <div className="form-group">
            <label htmlFor="recipientAccount">
              Recipients Bank Account Number
            </label>
            <input
              type="text"
              id="recipientAccount"
              placeholder="Enter recipient's account number"
              className="input-field"
              value={recipientAccount}
              onChange={(e) => setRecipientAccount(e.target.value)}
            />
          </div>
          <div className="form-group">
            <label htmlFor="amount">Amount (₹)</label>
            <input
              type="text"
              id="amount"
              placeholder="Amount to transfer"
              className="input-field"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
            />
          </div>
        </section>
        <button type="submit" className="transfer-button">
          Transfer Funds
        </button>

        {error && <p className="error-message">{error}</p>}
        {successMessage && <p className="success-message">{successMessage}</p>}
      </form>
    </div>
  );
}

export default Payments;
