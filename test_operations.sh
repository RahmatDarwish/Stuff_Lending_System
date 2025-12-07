#!/bin/bash
# Interactive test script for Stuff Lending System
cd /home/abdi/MT/second_year/1dv607/assignment2/a2

# Test operations that don't require specific IDs
echo "=== Testing List Operations ==="
echo -e "2\n5\n10\n0" | ./gradlew run --quiet

echo -e "\n=== Testing Add Member ==="
echo -e "1\nTest User\n555-0123\ntest@example.com\n0" | ./gradlew run --quiet

echo -e "\n=== Testing Advance Day ==="
echo -e "11\n0" | ./gradlew run --quiet

echo -e "\n=== Testing Verbose Members ==="
echo -e "3\n0" | ./gradlew run --quiet
