#!/bin/bash
# Interactive Test Script for Stuff Lending System

echo "=== Starting Stuff Lending System Tests ==="

cd /home/abdi/MT/second_year/1dv607/assignment2/a2

echo "=== Test 1: Basic List Operations ==="
echo "Testing: List Members, List Items, List Contracts, Advance Day"
echo -e "2\n5\n10\n11\n0" | ./gradlew run --quiet
echo ""

echo "=== Test 2: Add Member ==="
echo "Adding a test member"
echo -e "1\nTest User\n555-1234\ntest@example.com\n2\n0" | ./gradlew run --quiet
echo ""

echo "=== Test 3: Verbose Member List ==="
echo "Showing detailed member information"
echo -e "3\n0" | ./gradlew run --quiet
echo ""

echo "=== Test 4: Multiple Day Advances ==="
echo "Advancing time multiple days"
echo -e "11\n11\n11\n0" | ./gradlew run --quiet
echo ""

echo "=== All Basic Tests Complete ==="
echo "For interactive testing with dynamic IDs, run: ./gradlew run"
