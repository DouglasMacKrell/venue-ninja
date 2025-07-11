-- VENUES
INSERT INTO venue (id, name) VALUES
('msg', 'Madison Square Garden'),
('yankee', 'Yankee Stadium'),
('barclays', 'Barclays Center'),
('redrocks', 'Red Rocks Amphitheatre'),
('radiocity', 'Radio City Music Hall'),
('citi', 'Citi Field'),
('att', 'AT&T Stadium'),
('crypto', 'Crypto.com Arena'),
('scg', 'Sydney Cricket Ground'),
('marvel', 'Marvel Stadium');

-- MSG
INSERT INTO seat_recommendation (venue_id, section, category, reason, estimated_price, tip) VALUES
('msg', '104', 'Lower Bowl', 'Best resale value & view of stage', '$250', 'Avoid row 20+ due to rigging obstruction'),
('msg', '212', 'Budget 200-Level', 'Good price-to-view ratio', '$120', 'Stick to first 5 rows for best view'),
('msg', 'VIP C', 'Courtside Celebrity', 'Ultimate fan experience', '$750', 'Dress sharp, cameras are always rolling');

-- YANKEE
INSERT INTO seat_recommendation (venue_id, section, category, reason, estimated_price, tip) VALUES
('yankee', 'Legends', 'Premium', 'Close to the field, luxury service', '$450', 'Includes all-you-can-eat buffet'),
('yankee', 'Upper Deck 423', 'Budget Shade', 'Great for day games', '$60', 'Covered from sun and rain'),
('yankee', '203', 'Bleacher Creatures Zone', 'Hardcore fan energy', '$45', 'Chant with the crowd or be left behind');

-- BARCLAYS
INSERT INTO seat_recommendation (venue_id, section, category, reason, estimated_price, tip) VALUES
('barclays', '118', 'Lower Bowl', 'Great view of the action', '$180', 'Behind team benches'),
('barclays', '210', 'Mid Bowl Value', 'Good elevation for basketball', '$110', 'Center seats best'),
('barclays', 'VIP Suite 1', 'Premium Suite', 'Private bar & food service', '$500', 'Enter via luxury gate');

-- RED ROCKS
INSERT INTO seat_recommendation (venue_id, section, category, reason, estimated_price, tip) VALUES
('redrocks', 'Center Terrace', 'Acoustic Sweet Spot', 'Best natural sound quality', '$140', 'Bring a cushion!'),
('redrocks', 'Upper Bowl', 'Scenic Views', 'Best sunset shots', '$95', 'Come early, stairs are steep'),
('redrocks', 'Lower Tier', 'Closest to stage', 'Raw energy near performers', '$180', 'Bring earplugs for bass!');

-- RADIO CITY
INSERT INTO seat_recommendation (venue_id, section, category, reason, estimated_price, tip) VALUES
('radiocity', 'Orchestra A', 'Front Orchestra', 'Close to the Rockettes', '$200', 'Rows A–E are prime'),
('radiocity', 'Balcony R', 'Side Balcony Gems', 'Great acoustics & value', '$85', 'Farther from crowd noise'),
('radiocity', 'Mezzanine Center', 'Balanced View', 'Eye-level with effects', '$120', 'Enter early for best pics');

-- CITI FIELD
INSERT INTO seat_recommendation (venue_id, section, category, reason, estimated_price, tip) VALUES
('citi', '111', 'Field Level', 'Right behind home plate', '$280', 'Watch the pitcher’s grip closely'),
('citi', 'Bleachers', 'Budget', 'Cheapest option in the park', '$40', 'Bring sunscreen, no shade'),
('citi', '305', 'Family Zone', 'Kiddie games nearby', '$65', 'Kids eat free days are frequent');

-- AT&T STADIUM
INSERT INTO seat_recommendation (venue_id, section, category, reason, estimated_price, tip) VALUES
('att', 'C210', 'Club Level VIP', 'Upscale amenities & views', '$325', 'Includes access to lounges'),
('att', '50-Yard Mid', 'Center Field Premium', 'Perfect strategic view', '$480', 'Watch both sidelines like a coach'),
('att', 'End Zone 148', 'Red Zone Budget', 'Best chance for TD catches', '$115', 'Bring gloves for souvenirs');

-- CRYPTO.COM ARENA
INSERT INTO seat_recommendation (venue_id, section, category, reason, estimated_price, tip) VALUES
('crypto', 'Courtside', 'Celebrity Experience', 'Star sightings and up-close action', '$900', 'Cameras always rolling'),
('crypto', '325', 'Upper Bowl Steal', 'Budget with solid views', '$55', 'Watch for halftime deals'),
('crypto', '110', 'Team Bench View', 'Watch players up close', '$220', 'Arrive early for warmups');

-- SYDNEY CRICKET GROUND
INSERT INTO seat_recommendation (venue_id, section, category, reason, estimated_price, tip) VALUES
('scg', 'Members Pavilion', 'Historic VIP', 'Old-world charm and exclusivity', '$300', 'Strict dress code enforced'),
('scg', 'The Hill', 'GA Lawn', 'Bring a blanket, chill out', '$50', 'Arrive early for shade'),
('scg', 'Trumper Stand', 'Covered Mid-Tier', 'Balanced view + weatherproof', '$120', 'Perfect for all-day tests');

-- MARVEL STADIUM
INSERT INTO seat_recommendation (venue_id, section, category, reason, estimated_price, tip) VALUES
('marvel', 'Medallion Club', 'AFL Elite', 'Centrally located and catered', '$340', 'Includes bar access'),
('marvel', 'Cheer Squad', 'Fan Section', 'Most energetic area', '$60', 'Wear team colors or be ready to sing'),
('marvel', 'Level 2A', 'Premium Mid-Tier', 'Great elevation and amenities', '$150', 'Best views in the house');
