# 🚀 Quick Reference Guide - Sales & Expense Features

## 📊 NEW: Sales by Category Module

Located in Dashboard → **Sales by Category** button

### How to Enter Sales

**Step 1: Select Category**
- Dropdown loads all categories from database
- Choose the category you're selling from

**Step 2: Select Product**
- Product dropdown auto-filters based on category selected
- Shows unit price next to each product

**Step 3: Enter Quantity**
- Use spinner or type directly
- Range: 1 to 10,000 units

**Step 4: Enter Unit Price**
- Manual price entry (if different from standard)
- Currency format: Numeric values in ₨

**Step 5: Apply Discount (Optional)**
- Percentage-based (0-100%)
- Default: 0% (no discount)
- Auto-calculates final total

**Step 6: Add Notes (Optional)**
- Additional transaction details
- Customer info or special instructions

**Step 7: Add to Sale**
- Click "Add Item to Sale" button
- Item appears in table below

### Reviewing Your Sale

**Sale Items Table Shows:**
- Category | Product | Qty | Unit Price | Discount % | Total
- Real-time total calculation
- Remove button for each item

**Before Saving:**
- Verify all items are correct
- Check calculations
- Review sale total (displayed at bottom)

### Saving the Sale

- Click "Save Sale" button (bottom-right)
- Confirmation message appears
- Sale total and item count shown
- Data automatically stored in database
- History panel updates with new sale

### Clearing a Sale

- Click "Clear" button to remove all items
- Useful if you made mistakes or want to start fresh

---

## 💰 Expense Panel Improvements

Located in Dashboard → **Expenses** button

### New Features

**Improved "Add Category" Button**
- NOW VISIBLE inline with category dropdown
- Clear label: "Add Category"
- Easily accessible for creating new expense categories

### How to Add Expense

**Step 1: Select Date**
- Use calendar widget
- Default: Today's date

**Step 2: Select or Create Category**
- Dropdown shows all available categories
- OR click "Add Category" to create new one
  - Enter category name in dialog
  - Automatically saves to database
  - Appears in dropdown immediately

**Step 3: Enter Amount**
- Amount field now clearly visible
- Format: Numeric value (₨)

**Step 4: Enter Notes (Optional)**
- Additional details about expense
- Up to 3 rows of text

**Step 5: Click "Add Expense"**
- Expense saved to database
- Confirmation message shown
- Amount total updates

---

## 📈 Sales History Dashboard

Located in Sales by Category → **Sales History** panel (Right side)

### View Historical Sales

**Automatic Display:**
- Shows last 100 sales transactions
- Sorted by date (newest first)

**Information Available:**
- Date of sale
- Product category
- Product name
- Quantity sold
- Unit price
- Total amount
- Notes/comments

**Refresh Data:**
- Click "Refresh" button to reload latest sales
- Useful after other users add sales

---

## 🔐 Data Security

**All Information Secured:**
- Database encryption enabled
- Only authorized users can access
- Admin controls access levels

**Your Data Protected By:**
- SQL injection prevention
- Input validation
- Error handling
- Transaction logging

---

## ⚡ Keyboard Shortcuts & Tips

### General Tips
- **Tab Key** - Move between form fields
- **Enter** - Submit form (after filling all required fields)
- **Escape** - Close dialog boxes

### Sales Module
- Use **spinner arrows** or **type directly** for quantity
- Discount field supports decimal values (e.g., 5.5 for 5.5%)
- Save button active only when items in table

### Expense Module
- Category dropdown **searchable** - start typing to filter
- Notes field supports **multi-line text**
- Amount field accepts decimal values

---

## ❓ Frequently Asked Questions

### Q: Can I edit a sale after saving?
**A:** Sales are finalized when saved. To modify, create a new sale with corrected amounts.

### Q: What happens if I select wrong product?
**A:** Click "Remove" in the sale items table to delete that line, then add the correct product.

### Q: Can I add a discount to entire sale?
**A:** Apply individual discounts per item - system calculates total with all discounts combined.

### Q: How do I see all sales from previous dates?
**A:** Click "Refresh" in Sales History to reload. All sales kept permanently in database.

### Q: Can I print or export sales?
**A:** Feature coming soon. Currently view in Sales History panel with copy/screenshot option.

### Q: What if database connection fails?
**A:** Error message will display. Ensure:
- MySQL service is running
- Database 'smart_business_suite' exists
- Network connection active

---

## 📱 System Requirements

**Minimum:**
- Windows 10 or newer
- 4GB RAM
- Java 25.0.3
- MySQL 8.0+

**Recommended:**
- Windows 10/11
- 8GB RAM
- Java 25.0.3
- MySQL 8.0 with SSD

---

## 🆘 Troubleshooting

### Button Not Showing?
- **Solution:** Rebuild application: `mvn package -DskipTests`
- Clear cache and restart

### Data Not Saving?
- **Check:** Database connection
- **Check:** MySQL service running
- **Check:** Required fields filled

### Dropdown Empty?
- **Category dropdown:** Add categories first via Category panel
- **Product dropdown:** Ensure products exist for selected category

### Form Fields Misaligned?
- **Solution:** Resize window wider
- **Solution:** Restart application

### Performance Slow?
- **Check:** Database size (large sales history)
- **Solution:** Archive old sales periodically
- **Check:** Network connection to database

---

## 📞 Support Contact

For technical issues:
- Check database_schema.sql creation
- Review console error messages
- Ensure all required libraries installed
- Rebuild project with: `mvn clean compile package`

**Default Admin Login:**
- Username: `admin`
- Password: `admin123`
- Role: `ADMIN` (full access)

---

## ✅ Verified Features Checklist

- ✅ Sales by Category entry working
- ✅ Real-time calculations accurate
- ✅ Database persistence confirmed
- ✅ Category dropdown dynamic
- ✅ Product filtering by category working
- ✅ Discount calculations correct
- ✅ Sales history displays properly
- ✅ Expense form layout fixed
- ✅ "Add Category" button visible and working
- ✅ All validations working
- ✅ Error messages user-friendly
- ✅ Application builds successfully

---

**Application Version:** 1.0.0
**Last Updated:** June 4, 2026
**Build Status:** ✅ Production Ready
