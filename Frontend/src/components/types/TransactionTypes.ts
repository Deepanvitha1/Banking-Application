export interface Transaction {
  transaction_id?: string;
  sender_account_id?: string;
  receiver_account_id?: string;
  transactional_note?: string;
  amount?: number;
}
