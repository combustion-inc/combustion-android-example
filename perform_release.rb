#!/usr/local/bin/ruby

new_version = ARGV[0]
build_dir = "app/build/outputs/apk/" # Where build artifacts go

if new_version.to_s == ''
    abort "Please provide a version"
end

system("which gh", [ :out, :err ] => File::NULL) or abort "'gh' not found; make sure you have the GitHub CLI tools installed"

files = ["release/app-release.apk", "debug/app-debug.apk"]
    
# Get the current version from the most recent tag on the current branch for this platform (timer/charger)
current_version = `git describe --abbrev=0`
if $? == 0
    # Strip the opening 'v'
    current_version = current_version[1..-1].chomp.gsub(/^[0-9a-z]+_([0-9.]+)-?.*$/, '\1')
    # Attempt to discard tags like "0.4.1_devBoard"
    current_version = current_version.partition("_").first
else
    puts "No existing tag in the repo"
    current_version = "0.0.0"
end

REGEX_VERSION_NUM = /^[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}$/

# Validate the new version number (@p version), and ensure that it's greater (semantically) than @p old_version
def validate_new_version_number(version, old_version)

    if version =~ REGEX_VERSION_NUM
        if old_version =~ REGEX_VERSION_NUM
            new_maj, new_min, new_min2 = version.split('.').map(&:to_i)
            old_maj, old_min, old_min2 = old_version.split('.').map(&:to_i)

            # Ensure new version number is greater than the old one
            if( (new_maj > old_maj) ||
                ((new_maj == old_maj) && (new_min > old_min)) ||
                ((new_maj == old_maj) && (new_min == old_min) && (new_min2 > old_min2)) )
                return true
            end
        else
            STDERR.puts "WARNING: unable to parse old version (#{old_version}); not checking against previous version"
            return true
        end
    end

    return false
end

# configurations are not currently used, but we'll leave it in for now
def build_package(version, build_dir)
    puts "-- Cleaning --"
    system("./gradlew clean") or clean_abort("Failed to clean builds", version, false)
    puts "-- Done cleaning --"

    puts "-- Assembling --"
    system("./gradlew assemble") or clean_abort("Failed to assemble", version, false)
    puts "-- Done Assembling --"

end

def create_github_release(version_tag, files, build_dir)
    s = "gh release create #{version_tag} --generate-notes"
    s << " --title \"Combustion Android Example #{version_tag}\""

    # Note: This interface is brittle as it depends on paths defined in the Makefile
    files.each {
        |file|
        s << " '#{build_dir}/#{file}'"
    }

    system(s)
end

def clean_abort(message, version, cleanup_master_commit)
    STDERR.puts message
    # Clean up the tag
    system("git tag -d #{version}")

    if cleanup_master_commit
        system("git push --delete origin #{version}")
    end

    abort "Version tag aborted."
end

if !validate_new_version_number(new_version, current_version)
    abort "Invalid version number #{new_version} (previous version: #{current_version})"
end

version_tag = "v#{new_version}"

# The tag must be created before the build happens because git describe is used to populate the version in the FW
system("git tag #{version_tag} -m \"Release for version #{version_tag}\"") or clean_abort("Failed to create version tag.", version_tag, false)

# Note: The abort calls in this function could probably be condensed down to here
build_package(new_version, build_dir)

# To create a release out of an annotated tag, it needs to be pushed to GitHub first
system("git push origin #{version_tag}") or abort "*** ERROR: Failed to push tag '#{version_tag}' to origin."

create_github_release(version_tag, files, build_dir)

puts
puts "================================================================"
puts "Current branch successfully tagged as version #{version_tag}"
puts "================================================================"
