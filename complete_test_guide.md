# Complete Testing Session for Stuff Lending System

## Session 1: Basic Operations Test

1. **Start Application**
   ```bash
   ./gradlew run
   ```

2. **Test Sequence**
   ```
   Choice: 2    # List Members (get IDs)
   Choice: 5    # List Items (get IDs)
   Choice: 1    # Add Member
   Data: 
   - Name: Test User
   - Phone: 555-9999
   - Email: test@example.com
   
   Choice: 2    # List Members again (see new member)
   Choice: 11   # Advance Day
   Choice: 3    # List Members Verbose
   Choice: 0    # Exit
   ```

## Session 2: Item Operations Test

1. **Get Current IDs**
   ```
   Choice: 2    # Note member IDs
   Choice: 5    # Note item IDs
   ```

2. **Create Item**
   ```
   Choice: 4
   Data:
   - Name: Test Laptop
   - Category: OTHER
   - Description: Testing laptop
   - Cost per day: 20.0
   - Owner ID: [use ID from step 1]
   ```

3. **View and Edit Item**
   ```
   Choice: 6    # View item details
   Data: [new item ID]
   
   Choice: 7    # Edit item
   Data: [same item ID]
   - Update name or cost
   ```

## Session 3: Contract Operations Test

1. **Setup**
   ```
   Choice: 2    # Get member IDs
   Choice: 5    # Get item IDs
   ```

2. **Create Contract**
   ```
   Choice: 9
   Data:
   - Borrower ID: [member who doesn't own the item]
   - Item ID: [any available item]
   - Start day: 1
   - End day: 5
   ```

3. **View Results**
   ```
   Choice: 10   # List contracts
   Choice: 6    # View item with contracts
   Data: [item ID used in contract]
   ```

## Session 4: Advanced Testing

1. **Multiple Operations**
   ```
   Choice: 1    # Add member
   Choice: 4    # Create item for new member
   Choice: 9    # Create contract
   Choice: 11   # Advance day
   Choice: 10   # List contracts
   Choice: 8    # Try to delete item with contract
   Choice: 0    # Exit
   ```

## Quick Test Commands

### Test Basic Functionality
```bash
echo -e "2\n5\n10\n11\n0" | ./gradlew run --quiet
```

### Test Add Member
```bash
echo -e "1\nQuick Test\n555-0000\nquick@test.com\n2\n0" | ./gradlew run --quiet
```

### Test List Operations
```bash
echo -e "2\n3\n5\n10\n0" | ./gradlew run --quiet
```
