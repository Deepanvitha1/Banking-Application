"use client";
import React, { useEffect, useState } from "react";
import axios from "axios";
import { useRouter } from "next/navigation";
import { getCookie } from "cookies-next";
import "@/components/transactions-component/Transaction.css";
import { Transaction } from "@/components/types/TransactionTypes";

const TransactionHistory = () => {
  const [accountType, setAccountType] = useState("current");
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [loggedInUserAccountNumber, setLoggedInUserAccountNumber] =
    useState("");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [error, setError] = useState<string>("");
  const router = useRouter();

  const token = getCookie("token");
  const userId = getCookie("userId");

  useEffect(() => {
    if (!token) {
      router.push("/login");
    }
  }, [router, token]);

  useEffect(() => {
    if (!token) return;

    const fetchTransactionHistory = async () => {
      try {
        const res = await axios.get(`/api/transactions`, {
          params: { uid: userId, account_type: accountType, page },
        });

        if (res.data.success) {
          const data = res.data.data.data;
          setTransactions(data.items || []);
          setLoggedInUserAccountNumber(
            data.loggedInUserAccountNumber?.trim() || ""
          );
          setTotalPages(data.TotalPages || 1);
        } else {
          setTransactions([]);
        }
      } catch (error) {
        setTransactions([]);
        setError(
          "There was an issue fetching your transactions. Please try again later." +
            error
        );
      }
    };

    fetchTransactionHistory();
  }, [accountType, page, token, userId]);

  return (
    <div className="transactionsContainer">
      <div className="topTransactionContainer">
        <div className="transactionsContainerMessage">
          <h1 className="transactionsContainerMessageH1">
            Transaction History
          </h1>
        </div>
        <div>
          <select
            onChange={(e) => setAccountType(e.target.value)}
            className="selectAccountType"
          >
            <option value="current">Current Account</option>
            <option value="savings">Savings Account</option>
          </select>
        </div>
      </div>

      {error && <p className="error-message">{error}</p>}

      {transactions.length === 0 ? (
        <p className="noTransactions">No transactions made yet.</p>
      ) : (
        <div className="transactionHistory">
          <table className="transactionTable">
            <thead>
              <tr>
                <th>Transaction ID</th>
                <th>Sender Account Number</th>
                <th>Receiver Account Number</th>
                <th>Amount</th>
                <th>Type</th>
                <th>Note</th>
              </tr>
            </thead>
            <tbody>
              {transactions.map((transaction) => {
                const isDebit =
                  loggedInUserAccountNumber.trim() ===
                  transaction.sender_account_id?.trim();
                return (
                  <tr key={transaction.transaction_id}>
                    <td>{transaction.transaction_id}</td>
                    <td>{transaction.sender_account_id}</td>
                    <td>{transaction.receiver_account_id}</td>
                    <td className={isDebit ? "debit" : "credit"}>
                      {isDebit
                        ? `-₹${transaction.amount?.toFixed(2)}`
                        : `+₹${transaction.amount?.toFixed(2)}`}{" "}
                      INR
                    </td>
                    <td className={isDebit ? "transfer" : "receive"}>
                      {isDebit ? "Debited" : "Credited"}
                    </td>
                    <td>{transaction.transactional_note || "N/A"}</td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}

      {transactions.length > 0 && (
        <div className="paginationControls">
          <button disabled={page === 0} onClick={() => setPage(page - 1)}>
            Previous
          </button>
          <span>
            Page {page + 1} of {totalPages}
          </span>
          <button
            disabled={page >= totalPages - 1}
            onClick={() => setPage(page + 1)}
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
};

export default TransactionHistory;
